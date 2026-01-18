package com.domain.chat.app.pushNotification.entity;

import com.domain.chat.app.user.entity.UserEntity;
import com.domain.mapper.references.MasterEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "push_notifications", uniqueConstraints = @UniqueConstraint(columnNames = "endpoint"))
public class PushNotificationEntity implements MasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(length = 1000, nullable = false)
    private String endpoint;

    @Column(length = 1000, nullable = false)
    private String p256dh;

    @Column(length = 1000, nullable = false)
    private String auth;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
