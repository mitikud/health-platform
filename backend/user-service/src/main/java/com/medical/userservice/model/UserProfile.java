package com.medical.userservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String userId; // Link to AuthService User

    private String fullName;

    private int age;
    private String gender;

    private String preferredLanguage; // en, am, ti

    @ElementCollection
    private List<String> allergies;

    @ElementCollection
    private List<String> conditions;

    @ElementCollection
    private List<String> activeMedications;
}

