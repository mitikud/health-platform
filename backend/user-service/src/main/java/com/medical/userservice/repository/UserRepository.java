package com.medical.userservice.repository;

import com.medical.userservice.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByUserId(String userId);
}

