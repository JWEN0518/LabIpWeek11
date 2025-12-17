package com.example.healthhub.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainer")
public class Trainer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required.")
    @Column(nullable = false)
    private String name;

    @NotEmpty(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "certification")
    private String certification;

    @Min(value = 0, message = "Experience must be positive.")
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Program> programs = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutPlan> workoutPlans = new ArrayList<>();

    public Trainer() {}

    public Trainer(String name, String email, String specialization) {
        this.name = name;
        this.email = email;
        this.specialization = specialization;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getCertification() { return certification; }
    public void setCertification(String certification) { this.certification = certification; }
    
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<Program> getPrograms() { return programs; }
    public void setPrograms(List<Program> programs) { this.programs = programs; }
    
    public List<WorkoutPlan> getWorkoutPlans() { return workoutPlans; }
    public void setWorkoutPlans(List<WorkoutPlan> workoutPlans) { this.workoutPlans = workoutPlans; }
}