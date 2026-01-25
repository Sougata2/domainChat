package com.domain.chat.app.file.dto;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.mapper.references.MasterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.domain.chat.app.file.entity.FileEntity}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDto implements Serializable, MasterDto {
    private Long id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private MessageDto message;
    private Instant createdAt;
    private Instant updatedAt;
}