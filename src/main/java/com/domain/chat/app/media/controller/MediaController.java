package com.domain.chat.app.media.controller;

import com.domain.chat.app.media.dto.MediaDto;
import com.domain.chat.app.media.service.MediaService;
import com.domain.chat.app.message.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
public class MediaController {
    private final MediaService service;

    @PostMapping("/send-media")
    public ResponseEntity<MessageDto> sendMedia(
            @RequestPart("message") MessageDto message,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ResponseEntity.ok(service.sendMedia(files, message));
    }

    @PostMapping("/upload")
    public ResponseEntity<List<MediaDto>> upload(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(service.uploadMedia(files));
    }

    @PostMapping("/send")
    public ResponseEntity<MessageDto> send(@RequestBody MessageDto dto) {
        return ResponseEntity.ok(service.send(dto));
    }
}
