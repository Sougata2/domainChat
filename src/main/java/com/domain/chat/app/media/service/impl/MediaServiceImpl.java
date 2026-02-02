package com.domain.chat.app.media.service.impl;

import com.domain.chat.app.media.entity.MediaEntity;
import com.domain.chat.app.media.properties.MediaProperties;
import com.domain.chat.app.media.repository.MediaRepository;
import com.domain.chat.app.media.service.MediaService;
import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MessageService messageService;
    private final MediaRepository repository;
    private final MediaProperties properties;

    @Override
    @Transactional
    public MessageDto sendMedia(List<MultipartFile> files, MessageDto messageDto) {
        MessageEntity outgoing = messageService.sendAndReturnEntity(messageDto, "MEDIA");

        files.forEach(file -> {
            Path root = Paths.get(properties.getUploadDirectory());
            try {
                if (Files.notExists(root)) {
                    Files.createDirectories(root);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String extension;
            if (file.getOriginalFilename() != null && !file.getOriginalFilename().isBlank()) {
                extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                // add extension validation if required later.
            } else {
                throw new RuntimeException("File name cannot be empty");
            }


            String checksum;
            try (InputStream inputStream = file.getInputStream()) {
                checksum = DigestUtils.sha256Hex(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Path targetFile = root.resolve(Path.of(checksum + extension));
            if (Files.notExists(targetFile)) {
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, targetFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            MediaEntity media = MediaEntity.builder()
                    .size(file.getSize())
                    .originalName(file.getOriginalFilename())
                    .url(targetFile.toString())
                    .mimeType(file.getContentType())
                    .message(outgoing)
                    .build();

            repository.save(media);

        });
        return messageService.findById(outgoing.getId());
    }
}
