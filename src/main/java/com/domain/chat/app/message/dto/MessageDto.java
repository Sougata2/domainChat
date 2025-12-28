package com.domain.chat.app.message.dto;

import com.domain.chat.app.room.dto.RoomDto;
import com.domain.chat.app.user.dto.UserDto;
import com.domain.mapper.references.MasterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.domain.chat.app.message.entity.MessageEntity}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable, MasterDto {
    private Long id;
    private String uuid;
    private String message;
    private UserDto sender;
    private RoomDto room;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String senderEmail;
}