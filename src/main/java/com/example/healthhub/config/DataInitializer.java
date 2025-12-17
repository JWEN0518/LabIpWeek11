package com.example.healthhub.config;

import com.example.healthhub.dao.*;
import com.example.healthhub.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Component
public class DataInitializer {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private TrainerDAO trainerDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private ProgramDAO programDAO;

    @EventListener
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initializeData();
    }

    private void initializeData() {
        try {
            if (memberDAO.findAll().size() > 0) {
                System.out.println("✓ Database already initialized. Skipping data initialization.");
                return;
            }

            System.out.println("=== INITIALIZING TEST DATA ===");

            Member memberUser = new Member("Test Member", "userpass", "userpass", "member@test.com");
            memberUser.setPhone("01123456789");
            memberUser.setMemberType("student");
            memberUser.setYearOfBirth(2000);
            memberUser.setGender("Male");
            memberUser.setIsActive(true);
            memberDAO.save(memberUser);
            System.out.println("✓ Created member: userpass");

            Member memberUser2 = new Member("Alice Johnson", "alice", "alice123", "alice@test.com");
            memberUser2.setPhone("01187654321");
            memberUser2.setMemberType("staff");
            memberUser2.setYearOfBirth(1995);
            memberUser2.setGender("Female");
            memberUser2.setIsActive(true);
            memberDAO.save(memberUser2);
            System.out.println("✓ Created member: alice");

            Admin adminUser = new Admin("Admin User", "admin", "admin123", "admin@test.com");
            adminUser.setPhone("0112222222");
            adminUser.setIsActive(true);
            adminDAO.save(adminUser);
            System.out.println("✓ Created admin: admin");

            Trainer trainerUser = new Trainer("John Trainer", "trainer@test.com", "Fitness");
            trainerUser.setUsername("trainer");
            trainerUser.setPassword("trainer123");
            trainerUser.setPhone("0113333333");
            trainerUser.setIsActive(true);
            trainerDAO.save(trainerUser);
            System.out.println("✓ Created trainer: trainer");

            Category cardioCategory = new Category("Cardio", "Cardiovascular exercises");
            categoryDAO.save(cardioCategory);
            System.out.println("✓ Created category: Cardio");

            Category strengthCategory = new Category("Strength", "Weight training and resistance exercises");
            categoryDAO.save(strengthCategory);
            System.out.println("✓ Created category: Strength");

            Program program1 = new Program("Beginner Cardio", "A 4-week beginner cardio program", 4, 50.0);
            program1.setDifficultyLevel("Beginner");
            program1.setMaxParticipants(20);
            program1.setTrainer(trainerUser);
            program1.setCategory(cardioCategory);
            program1.setIsActive(true);
            programDAO.save(program1);
            System.out.println("✓ Created program: Beginner Cardio");

            Program program2 = new Program("Advanced Strength", "A 6-week advanced strength training program", 6, 75.0);
            program2.setDifficultyLevel("Advanced");
            program2.setMaxParticipants(15);
            program2.setTrainer(trainerUser);
            program2.setCategory(strengthCategory);
            program2.setIsActive(true);
            programDAO.save(program2);
            System.out.println("✓ Created program: Advanced Strength");

            System.out.println("=== TEST DATA INITIALIZATION COMPLETE ===");

        } catch (Exception e) {
            System.err.println("❌ Error initializing test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
