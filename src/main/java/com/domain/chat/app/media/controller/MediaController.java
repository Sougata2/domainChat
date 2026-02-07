package com.domain.chat.app.media.controller;

import com.domain.chat.app.media.dto.MediaDto;
import com.domain.chat.app.media.properties.MediaProperties;
import com.domain.chat.app.media.service.MediaService;
import com.domain.chat.app.message.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
public class MediaController {
    private final MediaService service;
    private final MediaProperties properties;

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

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String filename,
            @RequestParam(required = false) boolean download
    ) throws IOException {
        Path filePath = Paths.get(properties.getUploadDirectory()).resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) throw new RuntimeException("File not found");

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";

        String disposition = download ? "attachment" : "inline";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", disposition + "; filename=\"" + filename + "\"")
                .body(resource);
    }

}
