package com.domain.chat.app.pushNotification.service;

import com.domain.chat.app.pushNotification.dto.PushSubscriptionDto;
import com.domain.chat.app.user.entity.UserEntity;

public interface PushNotificationService {
    void subscribe(PushSubscriptionDto dto);

    void notifyUser(UserEntity user, String title, String body);
}
