package com.example.healthhub.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "bmi_record")
public class BmiRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required.")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Year of Birth is required.")
    @Min(value = 1900, message = "Please enter a valid Year of Birth.")
    @Max(value = 2025, message = "Please enter a valid Year of Birth.")
    @Column(name = "year_of_birth", nullable = false)
    private Integer yearOfBirth;

    @NotNull(message = "Weight is required.")
    @DecimalMin(value = "0.1", inclusive = false, message = "Weight must be positive.")
    @Column(nullable = false)
    private Double weight;

    @NotNull(message = "Height is required.")
    @DecimalMin(value = "0.1", inclusive = false, message = "Height must be positive.")
    @Column(nullable = false)
    private Double height;

    @NotEmpty(message = "Gender is required.")
    @Column(nullable = false)
    private String gender;
    
    @Column(name = "interests")
    private String interests; 

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public BmiRecord() {
        this.recordedAt = LocalDateTime.now();
    }

    public BmiRecord(String name, int yearOfBirth, double weight, double height, String gender) {
        this();
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }

    public int getAge() {
        return LocalDate.now().getYear() - this.yearOfBirth;
    }

    public double getBmi() {
        if (this.height <= 0) {
            return 0;
        }
        return this.weight / (this.height * this.height);
    }

    public String getCategory() {
        double bmi = getBmi();
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    public String[] getInterestsArray() {
        if (interests == null || interests.isEmpty()) {
            return new String[0];
        }
        return interests.split(",");
    }

    public void setInterestsArray(String[] interestsArray) {
        if (interestsArray == null || interestsArray.length == 0) {
            this.interests = "";
        } else {
            this.interests = String.join(",", interestsArray);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(Integer yearOfBirth) { this.yearOfBirth = yearOfBirth; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
}