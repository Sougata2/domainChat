package com.domain.chat.app.message.entity;

import com.domain.chat.app.room.entity.RoomEntity;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.mapper.references.MasterEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class MessageEntity implements MasterEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String message;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,  CascadeType.REFRESH,  CascadeType.DETACH})
    @JoinColumn(name = "user_id")
    private UserEntity sender;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,  CascadeType.REFRESH,  CascadeType.DETACH})
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
