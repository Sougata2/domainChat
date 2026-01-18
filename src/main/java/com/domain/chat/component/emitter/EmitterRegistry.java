package com.domain.chat.component.emitter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EmitterRegistry {
    private final Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String username) {
        SseEmitter emitter = new SseEmitter(0L);

        emitter.onCompletion(() -> removeEmitter(username, emitter));
        emitter.onTimeout(() -> removeEmitter(username, emitter));
        emitter.onError(e -> removeEmitter(username, emitter));

        userEmitters
                .computeIfAbsent(username, u -> new CopyOnWriteArrayList<>())
                .add(emitter);

        try {
            emitter.send(SseEmitter.event().name("INIT").data("connected"));
        } catch (IOException e) {
            removeEmitter(username, emitter);
        }
        return emitter;
    }

    public void broadcast(String username, Object data) {
        broadcast(username, "MESSAGE", data);
    }

    public void broadcast(String username, String eventName, Object data) {
        List<SseEmitter> emitters = userEmitters.get(username);
        if (emitters == null) return;

        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }

    public void removeEmitter(String username, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(username);
        if (emitters == null) return;
        emitters.remove(emitter);
    }

    @Scheduled(fixedRate = 15000)
    public void heartbeat() {
        userEmitters.forEach((username, emitters) -> {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(
                            SseEmitter.event().name("HEARTBEAT").data("ping")
                    );
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }
            emitters.removeAll(deadEmitters);

            if (emitters.isEmpty()) {
                userEmitters.remove(username);
            }
        });
    }
}
