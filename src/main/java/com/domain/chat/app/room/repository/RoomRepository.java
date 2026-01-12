package com.domain.chat.app.room.repository;

import com.domain.chat.app.room.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    @Query("select e from RoomEntity e where e.referenceNumber = :referenceNumber")
    Optional<RoomEntity> findByReferenceNumber(UUID referenceNumber);

    @Query("select e from RoomEntity e " +
            "join fetch e.participants f " +
            "where exists (" +
            "    select 1" +
            "    from e.participants p2" +
            "    where p2.id = :userId" +
            ")")
    List<RoomEntity> findByUserId(Long userId);

    @Query("""
            select re
            from RoomEntity re
            join re.participants p
            where re.roomType = :roomType
              and p.id in (:participants)
            group by re.id
            having count(distinct p.id) = 2
            """)
    Optional<RoomEntity> findRoomOpt(Set<Long> participants, String roomType);

    @Query("select distinct e from RoomEntity e " +
            "join fetch e.participants f " +
            "left join fetch e.messages m " +
            "where exists (" +
            "    select 1 from e.participants p2 " +
            "    where p2.id = :userId" +
            ") " +
            "and (m is null or m.createdAt = (" +
            "    select max(m2.createdAt) from MessageEntity m2 " +
            "    where m2.room.id = e.id" +
            "))")
    List<RoomEntity> findByUserIdWithLatestMessage(Long userId);

    @Query("""
            select e.referenceNumber
            from RoomEntity e
            join e.participants p
            where p.id = :userId
            order by e.lastMessageSentAt desc
            """)
    List<UUID> getSubscribedRoomsReference(Long userId);
}
