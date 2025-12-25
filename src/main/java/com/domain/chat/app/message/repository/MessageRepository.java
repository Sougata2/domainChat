package com.domain.chat.app.message.repository;

import com.domain.chat.app.message.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("select e from MessageEntity e where e.room.referenceNumber = :referenceNumber order by e.createdAt desc")
    List<MessageEntity> findByRoomReferenceNumber(String referenceNumber);

    @Query("select e from MessageEntity e where e.room.referenceNumber = :referenceNumber order by e.createdAt desc")
    List<MessageEntity> findByRoomReferenceNumberDesc(String referenceNumber);
}
