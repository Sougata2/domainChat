package com.domain.chat.app.media.dto;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.mapper.references.MasterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.domain.chat.app.media.entity.MediaEntity}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto implements Serializable, MasterDto {
    private Long id;
    private String url;
    private String originalName;
    private Long size;
    private MessageDto message;
    private String mimeType;
    private Instant createdAt;
    private Instant updatedAt;
}