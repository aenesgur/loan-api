package com.aenesgur.banking.loan.repository;

import com.aenesgur.banking.loan.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("select u from User u where u.username = :username")
    Optional<User> findByUsername(String username);

    @Query("select u.id from User u where u.username = :username")
    Optional<UUID> findIdByUsername(String username);
}