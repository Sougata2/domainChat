package com.domain.chat.app.pushNotification.dto;

import com.domain.chat.app.user.dto.UserDto;
import com.domain.mapper.references.MasterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.domain.chat.app.pushNotification.entity.PushNotificationEntity}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationDto implements Serializable, MasterDto {
    private Long id;
    private UserDto user;
    private String endpoint;
    private String p256dh;
    private String auth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}