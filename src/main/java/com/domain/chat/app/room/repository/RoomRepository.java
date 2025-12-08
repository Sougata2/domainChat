package com.domain.chat.app.room.repository;

import com.domain.chat.app.room.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    @Query("select e from RoomEntity e where e.referenceNumber = :referenceNumber")
    Optional<RoomEntity> findByReferenceNumber(String referenceNumber);
}
