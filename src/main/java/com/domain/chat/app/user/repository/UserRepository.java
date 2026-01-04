package com.domain.chat.app.user.repository;

import com.domain.chat.app.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("select e from UserEntity e where e.email = :email")
    Optional<UserEntity> findByEmail(String email);

    @Query("select e from UserEntity e where e.email <> :email order by e.email")
    List<UserEntity> findAllExceptLoggedInUser(String email);
}