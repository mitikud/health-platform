package com.medical.userservice.service;

import com.medical.userservice.dto.UserDto;
import com.medical.userservice.model.UserProfile;
import com.medical.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto createUser(UserDto dto) {
        UserProfile profile = UserProfile.builder()
                .userId(dto.getUserId())
                .fullName(dto.getFullName())
                .age(dto.getAge())
                .gender(dto.getGender())
                .preferredLanguage(dto.getPreferredLanguage())
                .allergies(dto.getAllergies())
                .conditions(dto.getConditions())
                .activeMedications(dto.getActiveMedications())
                .build();
        userRepository.save(profile);
        dto.setId(profile.getId());
        return dto;
    }

    public UserDto getUserById(String id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDto getUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserDto toDto(UserProfile profile) {
        return UserDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .age(profile.getAge())
                .gender(profile.getGender())
                .preferredLanguage(profile.getPreferredLanguage())
                .allergies(profile.getAllergies())
                .conditions(profile.getConditions())
                .activeMedications(profile.getActiveMedications())
                .build();
    }
}

