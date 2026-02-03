package com.domain.chat.app.media.entity;

import com.domain.chat.app.media.enums.MediaStatus;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.mapper.references.MasterEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media")
public class MediaEntity implements MasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column
    private String originalName;

    @Column
    private Long size;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private MessageEntity message;

    @Column
    private String mimeType;

    @Column
    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
