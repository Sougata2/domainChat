package com.domain.chat.app.user.service.impl;

import com.domain.chat.app.user.service.UserService;
import com.domain.chat.component.emitter.EmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EmitterRegistry emitterRegistry;

    @Override
    public SseEmitter stream() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return emitterRegistry.addEmitter(username);
    }
}
