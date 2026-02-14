package com.domain.chat.app.user.service;

import com.domain.chat.app.user.dto.PresenceDto;
import com.domain.chat.app.user.dto.UserDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface UserService {
    SseEmitter stream();

    List<UserDto> getContacts();

    List<PresenceDto> getPresence();
}
