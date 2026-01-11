package com.domain.chat.app.room.dto;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomOptDto {
    private Long id;
    private String groupName;
    private String referenceNumber;
    private List<UserDto> participants;
    private List<String> uuids;
    private Map<String, MessageDto> messages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
