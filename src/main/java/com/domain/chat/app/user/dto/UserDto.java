package com.domain.chat.app.user.dto;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.pushNotification.dto.PushNotificationDto;
import com.domain.chat.app.role.dto.RoleDto;
import com.domain.chat.app.room.dto.RoomDto;
import com.domain.mapper.references.MasterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.domain.chat.app.user.entity.UserEntity}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable, MasterDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<RoleDto> roles;
    private Set<PushNotificationDto> pushNotifications;
    private RoleDto defaultRole;
    private Set<MessageDto> messages;
    private Set<RoomDto> rooms;
    private LocalDateTime lastSeen;
    private Boolean isOnline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}