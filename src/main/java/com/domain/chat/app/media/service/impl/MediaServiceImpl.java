package com.domain.chat.app.media.service.impl;

import com.domain.chat.app.media.dto.MediaDto;
import com.domain.chat.app.media.entity.MediaEntity;
import com.domain.chat.app.media.enums.MediaStatus;
import com.domain.chat.app.media.properties.MediaProperties;
import com.domain.chat.app.media.repository.MediaRepository;
import com.domain.chat.app.media.service.MediaService;
import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.message.repository.MessageRepository;
import com.domain.chat.app.message.service.MessageService;
import com.domain.mapper.service.MapperService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final MediaRepository repository;
    private final MediaProperties properties;
    private final MapperService mapper;

    @Override
    @Transactional
    public MessageDto sendMedia(List<MultipartFile> files, MessageDto messageDto) {
        // 1. upload media first with empty message
        Set<MediaEntity> mediaEntities = new LinkedHashSet<>();
        for (MultipartFile file : files) {
            mediaEntities.add(upload(file));
        }
        Set<MediaEntity> savedMedia = new LinkedHashSet<>(repository.saveAll(mediaEntities));

        // 2. Create the outgoing message.
        MessageEntity outgoing = messageService.prepareMessageEntity(messageDto, "MEDIA");
        // 2. Add the Media Entities to the message.
        outgoing.setMedia(savedMedia);

        MessageEntity saved = messageRepository.save(outgoing);
        return (MessageDto) mapper.toDto(saved);
    }

    @Override
    @Transactional
    public List<MediaDto> uploadMedia(List<MultipartFile> files) {
        Set<MediaEntity> entities = new LinkedHashSet<>();
        for (MultipartFile file : files) {
            entities.add(upload(file));
        }
        List<MediaEntity> saved = repository.saveAll(entities);

        return saved.stream().map(e -> (MediaDto) mapper.toDto(e)).toList();
    }

    @Override
    @Transactional
    public MessageDto send(MessageDto dto) {
        MessageEntity message = messageService.prepareMessageEntity(dto, "MEDIA");
        // remove the detached entity(media)
        message.setMedia(null);
        MessageEntity saved = messageRepository.save(message);
        Set<MediaEntity> mediaList = new LinkedHashSet<>();
        for (MediaDto media : dto.getMedia()) {
            MediaEntity mediaEntity = repository.findById(media.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Media not found"));
            mediaEntity.setStatus(MediaStatus.ATTACHED);
            mediaEntity.setMessage(saved);
            mediaList.add(mediaEntity);
        }
        List<MediaEntity> attachedMedia = repository.saveAll(mediaList);
        saved.setMedia(new LinkedHashSet<>(attachedMedia));
        return (MessageDto) mapper.toDto(saved);
    }

    private MediaEntity upload(MultipartFile file) {
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

        return MediaEntity.builder()
                .size(file.getSize())
                .status(MediaStatus.UPLOADED)
                .mimeType(file.getContentType())
                .originalName(file.getOriginalFilename())
                .url("/media/files/" + targetFile.getFileName())
                .build();
    }
}
