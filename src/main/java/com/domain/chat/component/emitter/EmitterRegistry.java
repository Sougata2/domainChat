package com.domain.chat.component.emitter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EmitterRegistry {
    private final Map<String, List<SseEmitter>> roomEmitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String roomRef) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        roomEmitters.computeIfAbsent(roomRef, id -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(roomRef, emitter));
        emitter.onTimeout(() -> removeEmitter(roomRef, emitter));
        emitter.onError((e) -> removeEmitter(roomRef, emitter));
        return emitter;
    }

    public void broadcast(String roomRef, Object data) {
        List<SseEmitter> emitters = roomEmitters.get(roomRef);
        if (emitters == null) return;

        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(data));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }

    public void removeEmitter(String roomRef, SseEmitter emitter) {
        List<SseEmitter> emitters = roomEmitters.get(roomRef);
        if (emitters == null) return;
        emitters.remove(emitter);
    }
}
