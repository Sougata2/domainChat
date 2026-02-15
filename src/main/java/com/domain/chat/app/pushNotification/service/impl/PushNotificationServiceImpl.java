package com.domain.chat.app.pushNotification.service.impl;

import com.domain.chat.app.pushNotification.dto.PushSubscriptionDto;
import com.domain.chat.app.pushNotification.entity.PushNotificationEntity;
import com.domain.chat.app.pushNotification.properties.PushNotificationProperties;
import com.domain.chat.app.pushNotification.repository.PushNotificationRepository;
import com.domain.chat.app.pushNotification.service.PushNotificationService;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.chat.app.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {
    private final PushNotificationRepository repository;
    private final UserRepository userRepository;
    private final PushNotificationProperties properties;

    @Override
    @Transactional
    public void subscribe(PushSubscriptionDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User %s is not found".formatted(username)));

        repository.findByEndPointAndUserId(dto.getEndpoint(), user.getId())
                .ifPresentOrElse((existing) -> {
                    // already subscribed to push notification
                }, () -> {
                    PushNotificationEntity entity = PushNotificationEntity.builder()
                            .user(user)
                            .endpoint(dto.getEndpoint())
                            .p256dh(dto.getKeys().getP256dh())
                            .auth(dto.getKeys().getAuth())
                            .build();
                    repository.save(entity);
                });

    }

    @Async
    @Override
    public void notifyUser(UserEntity user, String title, String body) {
        List<PushNotificationEntity> subscriptions = repository.findByUserId(user.getId());

        for (PushNotificationEntity subscription : subscriptions) {
            sendNotification(subscription, title, body);
        }
    }

    private void sendNotification(PushNotificationEntity sub, String title, String body) {
        try {
            Subscription.Keys keys = new Subscription.Keys(sub.getP256dh(), sub.getAuth());
            Subscription subscription = new Subscription(sub.getEndpoint(), keys);
            String payload = """
                    {
                      "title": "%s",
                      "body": "%s",
                      "url": "/chat"
                    }
                    """.formatted(title, body);
            Notification notification = new Notification(subscription, payload);
            PushService pushService = new PushService();
            pushService.setPublicKey(properties.getPublicKey());
            pushService.setPrivateKey(properties.getPrivateKey());
            pushService.setSubject(properties.getSubject());
            pushService.send(notification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
