package com.domain.chat.app.file.service.impl;

import com.domain.chat.app.file.dto.FileDto;
import com.domain.chat.app.file.entity.FileEntity;
import com.domain.chat.app.file.properties.FileProperties;
import com.domain.chat.app.file.repository.FileRepository;
import com.domain.chat.app.file.service.FileService;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.mapper.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository repository;
    private final FileProperties properties;
    private final MapperService mapper;

    @Override
    @Transactional
    public FileDto upload(MultipartFile file, MessageEntity message) {
        Path root = Paths.get(properties.getTargetDirectory());
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

        FileEntity entity = FileEntity.builder()
                .fileName(file.getOriginalFilename())
                .filePath(targetFile.getFileName().toString())
                .message(message)
                .fileSize(file.getSize())
                .build();

        FileEntity saved = repository.save(entity);
        return (FileDto) mapper.toDto(saved);
    }

    @Override
    public File download(Long id) {
        FileEntity entity = repository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("File with id %d is not found".formatted(id))
                );
        Path path = Path.of(entity.getFilePath());
        return path.toFile();
    }
}
