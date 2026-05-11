package com.docify.docify_be.user.repository;

import com.docify.docify_be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndProvider(String email, com.docify.docify_be.user.entity.AuthProvider provider);
}
