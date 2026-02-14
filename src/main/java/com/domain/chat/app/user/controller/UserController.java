package com.domain.chat.app.user.controller;

import com.domain.chat.app.user.dto.PresenceDto;
import com.domain.chat.app.user.dto.UserDto;
import com.domain.chat.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping("/contacts")
    public ResponseEntity<List<UserDto>> getContacts() {
        return ResponseEntity.ok(service.getContacts());
    }

    @GetMapping("/presence")
    public ResponseEntity<List<PresenceDto>> getPresence() {
        return ResponseEntity.ok(service.getPresence());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return service.stream();
    }
}
