package com.domain.chat.app.presence.controller;

import com.domain.chat.app.presence.dto.VisibilityDto;
import com.domain.chat.app.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presence")
public class PresenceController {
    private final PresenceService service;

    @PostMapping("/visibility")
    public ResponseEntity<Void> updatePresence(@RequestBody VisibilityDto dto) {
        service.updateVisibility(dto.getActive());
        return ResponseEntity.ok().build();
    }
}
