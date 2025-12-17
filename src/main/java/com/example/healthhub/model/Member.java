package com.example.healthhub.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;



@Entity
@Table(name = "member")
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required.")
    @Column(nullable = false)
    private String name;

    @NotEmpty(message = "Username is required.")
    @Column(unique = true, nullable = false)
    private String username;

    @NotEmpty(message = "Password is required.")
    @Column(nullable = false)
    private String password;

    @Email(message = "Invalid email format.")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "member_type")
    private String memberType;

    @NotNull(message = "Year of Birth is required.")
    @Min(value = 1900)
    @Max(value = 2025)
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @NotEmpty(message = "Gender is required.")
    @Column(nullable = false)
    private String gender;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutPlan> workoutPlans = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("recordedAt DESC")
    private List<BmiRecord> bmiHistory = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Member() {}

    public Member(String name, String username, String password, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }

    public Integer getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(Integer yearOfBirth) { this.yearOfBirth = yearOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }

    public List<WorkoutPlan> getWorkoutPlans() { return workoutPlans; }
    public void setWorkoutPlans(List<WorkoutPlan> workoutPlans) { this.workoutPlans = workoutPlans; }

    public List<BmiRecord> getBmiHistory() { return bmiHistory; }
    public void setBmiHistory(List<BmiRecord> bmiHistory) { this.bmiHistory = bmiHistory; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

public int getAge() {
    if (yearOfBirth == null) return 0;
    return LocalDate.now().getYear() - this.yearOfBirth;
}

public double getBmi() {
    if (bmiHistory != null && !bmiHistory.isEmpty()) {
        return bmiHistory.get(0).getBmi();
    }
    return 0.0;
}

public String getCategory() {
    double bmi = getBmi();
    if (bmi < 18.5) return "Underweight";
    else if (bmi < 25) return "Normal weight";
    else if (bmi < 30) return "Overweight";
    else return "Obese";
}

public double getWeight() {
    if (bmiHistory != null && !bmiHistory.isEmpty()) {
        return bmiHistory.get(0).getWeight();
    }
    return 0.0;
}

public double getHeight() {
    if (bmiHistory != null && !bmiHistory.isEmpty()) {
        return bmiHistory.get(0).getHeight();
    }
    return 0.0;
}

public List<String> getInterests() {
    if (bmiHistory != null && !bmiHistory.isEmpty()) {
        String[] arr = bmiHistory.get(0).getInterestsArray();
        return arr != null ? Arrays.asList(arr) : new ArrayList<>();
    }
    return new ArrayList<>();
}
}