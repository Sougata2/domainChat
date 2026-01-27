package com.domain.chat.app.message.entity;

import com.domain.chat.app.file.entity.FileEntity;
import com.domain.chat.app.message.listener.MessageListener;
import com.domain.chat.app.room.entity.RoomEntity;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.mapper.references.MasterEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
@EntityListeners(MessageListener.class)
public class MessageEntity implements MasterEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column
    private String uuid;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "user_id")
    private UserEntity sender;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @OneToOne(mappedBy = "message", orphanRemoval = true, cascade = CascadeType.ALL)
    private FileEntity file;

    @Formula("(select e.email from users e where e.id = user_id)")
    private String senderEmail;

    @Formula("(select e.first_name from users e where e.id = user_id)")
    private String senderFirstName;

    @Formula("(select e.last_name from users e where e.id = user_id)")
    private String senderLastName;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
