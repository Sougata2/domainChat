package com.domain.chat.app.pushNotification.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "vapid")
public class PushNotificationProperties {
    private String publicKey;
    private String privateKey;
    private String subject;
}
