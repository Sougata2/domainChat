package com.domain.chat.app.media.controller;

import com.domain.chat.app.media.service.MediaService;
import com.domain.chat.app.message.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
public class MediaController {
    private final MediaService service;

    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMedia(
            @RequestPart("message") MessageDto message,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ResponseEntity.ok(service.sendMedia(files, message));
    }
}
