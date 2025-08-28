package com.medical.userservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String userId;
    private String fullName;
    private int age;
    private String gender;
    private String preferredLanguage;
    private List<String> allergies;
    private List<String> conditions;
    private List<String> activeMedications;
}

