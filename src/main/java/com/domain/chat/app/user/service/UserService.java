package com.domain.chat.app.user.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface UserService {
    SseEmitter stream();
}
