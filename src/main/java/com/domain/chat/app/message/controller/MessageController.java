package com.domain.chat.app.message.controller;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService service;

    @GetMapping("/all")
    public ResponseEntity<List<MessageDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/send")
    public ResponseEntity<MessageDto> send(@RequestBody MessageDto dto) {
        return ResponseEntity.ok(service.send(dto));
    }

    @PostMapping(value = "/send/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> sendFile(
            @RequestPart(value = "message") MessageDto message,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return ResponseEntity.ok(service.sendFile(message, file));
    }

    @PostMapping
    public ResponseEntity<MessageDto> create(@RequestBody MessageDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping
    public ResponseEntity<MessageDto> update(@RequestBody MessageDto dto) {
        return ResponseEntity.ok(service.update(dto));
    }

    @DeleteMapping
    public ResponseEntity<MessageDto> delete(@RequestBody MessageDto dto) {
        return ResponseEntity.ok(service.delete(dto));
    }
}
