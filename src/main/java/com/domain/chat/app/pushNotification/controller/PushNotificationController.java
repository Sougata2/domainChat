package com.domain.chat.app.pushNotification.controller;

import com.domain.chat.app.pushNotification.dto.PushSubscriptionDto;
import com.domain.chat.app.pushNotification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/push-notification")
public class PushNotificationController {
    private final PushNotificationService service;

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody PushSubscriptionDto dto) {
        service.subscribe(dto);
        return ResponseEntity.ok().build();
    }
}
