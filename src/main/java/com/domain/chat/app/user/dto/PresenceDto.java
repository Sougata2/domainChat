package com.domain.chat.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresenceDto {
    private String username;
    private Boolean isOnline;
    private LocalDateTime lastSeen;
}
