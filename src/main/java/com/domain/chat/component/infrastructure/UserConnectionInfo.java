package com.domain.chat.component.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class UserConnectionInfo {
    Set<SseEmitter> emitters = new LinkedHashSet<>();
    volatile long lastHeartbeat;
    volatile boolean online;
}
