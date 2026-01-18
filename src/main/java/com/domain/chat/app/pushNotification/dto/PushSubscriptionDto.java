package com.domain.chat.app.pushNotification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushSubscriptionDto {
    private String endpoint;
    private Keys keys;

    @Getter
    @Setter
    public static class Keys {
        private String p256dh;
        private String auth;
    }
}
