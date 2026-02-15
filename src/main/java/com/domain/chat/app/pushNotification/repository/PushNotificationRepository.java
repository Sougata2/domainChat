package com.domain.chat.app.pushNotification.repository;

import com.domain.chat.app.pushNotification.entity.PushNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotificationEntity, Long> {
    @Query("select e from PushNotificationEntity e where e.endpoint = :endPoint and e.user.id = :userId")
    Optional<PushNotificationEntity> findByEndPointAndUserId(String endPoint, Long userId);

    @Query("select e from PushNotificationEntity e where e.user.id = :userId")
    List<PushNotificationEntity> findByUserId(Long userId);
}
