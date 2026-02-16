package com.domain.chat.app.presence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisibilityDto {
    private Long userId;
    private Boolean active;
}
