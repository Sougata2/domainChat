package com.domain.chat.component.emitter;

import com.domain.chat.app.presence.service.PresenceService;
import com.domain.chat.component.infrastructure.Status;
import com.domain.chat.component.infrastructure.UserConnectionInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
@RequiredArgsConstructor
public class EmitterRegistry {
    private final Map<String, UserConnectionInfo> users = new ConcurrentHashMap<>();
    private final PresenceService presenceService;

    public SseEmitter addEmitter(String username) {
        SseEmitter emitter = new SseEmitter(30_000L);

        emitter.onCompletion(() -> removeEmitter(username, emitter));
        emitter.onTimeout(() -> removeEmitter(username, emitter));
        emitter.onError(e -> removeEmitter(username, emitter));

        UserConnectionInfo info = users.computeIfAbsent(username, u -> {
            UserConnectionInfo newInfo = new UserConnectionInfo();
            newInfo.setOnline(false);
            return newInfo;
        });

        info.getEmitters().add(emitter);
        info.setLastHeartbeat(System.currentTimeMillis());

        if (!info.isOnline()) {
            info.setOnline(true);
            broadcastPresence(username, Status.ONLINE, LocalDateTime.now());
        }
        return emitter;
    }

    public void broadcastPresence(String username, Status status, LocalDateTime lastSeen) {
        users.forEach((otherUser, info) -> {
            if (!otherUser.equals(username)) {
                for (SseEmitter emitter : info.getEmitters()) {
                    try {
                        emitter.send(
                                SseEmitter.event()
                                        .name("PRESENCE")
                                        .data(Map.of("username", username, "data", Map.of("status", status, "lastSeen", lastSeen)))
                        );
                    } catch (IOException ignored) {
                    }
                }
            }
        });
    }

    public void broadcast(String username, String eventName, Object data) {
        UserConnectionInfo info = users.get(username);
        if (info == null) return;

        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : info.getEmitters()) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        deadEmitters.forEach((de) -> info.getEmitters().remove(de));
    }

    private void removeEmitter(String username, SseEmitter emitter) {
        UserConnectionInfo info = users.get(username);
        if (info == null) return;
        info.getEmitters().remove(emitter);
    }

    @Scheduled(fixedRate = 2000)
    public void heartbeat() {
        long now = System.currentTimeMillis();
        long timeout = 6_000;

        List<String> offlineUsers = new ArrayList<>();

        users.forEach((username, info) -> {
            if (!info.isOnline() && info.getEmitters().isEmpty()) {
                offlineUsers.add(username);
                return;
            }
            List<SseEmitter> deadEmitters = new ArrayList<>();

            boolean atLeastOneAlive = false;
            for (SseEmitter emitter : info.getEmitters()) {
                try {
                    emitter.send(SseEmitter.event().name("HEARTBEAT").data("ping"));
                    atLeastOneAlive = true;
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }

            deadEmitters.forEach((de) -> info.getEmitters().remove(de));

            if (info.getEmitters().isEmpty()) {
                if (info.isOnline() && now - info.getLastHeartbeat() > timeout) {
                    info.setOnline(false);
                    broadcastPresence(username, Status.OFFLINE, LocalDateTime.now());
                    offlineUsers.add(username);
                    // persist the last seen to DB
                    presenceService.updatePresence(LocalDateTime.now(), username);
                }
            }

            if (atLeastOneAlive) {
                info.setLastHeartbeat(now);
            }
        });

        if (!offlineUsers.isEmpty()) {
            offlineUsers.forEach(users::remove);
        }
    }


}
