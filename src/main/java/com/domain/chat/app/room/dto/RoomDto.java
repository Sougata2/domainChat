package com.domain.chat.app.room.dto;

import com.domain.chat.app.user.dto.UserDto;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.mapper.references.MasterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.domain.chat.app.room.entity.RoomEntity}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto implements Serializable, MasterDto {
    private Long id;
    private String referenceNumber;
    private Set<UserDto> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}