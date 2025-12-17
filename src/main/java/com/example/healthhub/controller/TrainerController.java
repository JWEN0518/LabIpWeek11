package com.example.healthhub.controller;

import com.example.healthhub.dao.WorkoutPlanDAO;
import com.example.healthhub.dao.MemberDAO;
import com.example.healthhub.dao.TrainerDAO;
import com.example.healthhub.model.WorkoutPlan;
import com.example.healthhub.model.Trainer;
import com.example.healthhub.model.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/trainer")
public class TrainerController {
    
    @Autowired
    private WorkoutPlanDAO workoutPlanDAO;
    
    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private TrainerDAO trainerDAO;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        System.out.println("=== TRAINER DASHBOARD ACCESS ===");
        
        if (!"trainer".equals(session.getAttribute("userrole"))) {
            System.out.println("❌ Not authenticated as trainer - redirecting to login");
            return "redirect:/login";
        }
        
        Long trainerId = (Long) session.getAttribute("trainerId");
        
        if (trainerId == null) {
            System.err.println("❌ ERROR: Trainer ID is null in session!");
            session.invalidate();
            return "redirect:/login";
        }

        System.out.println("✅ Loading trainer dashboard for ID: " + trainerId);
        
        Trainer trainer = null;
        try {
            trainer = trainerDAO.findById(trainerId);
            System.out.println("✅ Trainer loaded from database: " + (trainer != null ? trainer.getName() : "null"));
        } catch (Exception e) {
            System.out.println("⚠️ Could not load trainer from DB: " + e.getMessage());
        }
        
        if (trainer == null) {
            System.out.println("⚠️ Using mock trainer data for demonstration");
            trainer = new Trainer();
            trainer.setId(trainerId);
            trainer.setName((String) session.getAttribute("username"));
            trainer.setEmail("trainer@healthhub.utm.my");
            trainer.setPhone("012-9999999");
            trainer.setSpecialization("Fitness & Strength Training");
            trainer.setCertification("Certified Personal Trainer (CPT)");
            trainer.setYearsOfExperience(5);
            trainer.setBio("Experienced fitness trainer specializing in strength and conditioning programs.");
            trainer.setUsername("trainer1");
            trainer.setIsActive(true);
        }
        
        model.addAttribute("trainer", trainer);
        
        long planCount = 0;
        try {
            planCount = workoutPlanDAO.countByTrainer(trainerId);
            System.out.println("✅ Plan count: " + planCount);
        } catch (Exception e) {
            System.out.println("⚠️ Could not count plans from DB: " + e.getMessage());
            planCount = 0;
        }
        
        model.addAttribute("planCount", planCount);

        return "trainer/dashboard";
    }
    
    @GetMapping("/plans")
public String listPlans(Model model, HttpSession session, @RequestParam(value = "keyword", required = false) String keyword) {
    System.out.println("=== TRAINER PLANS ACCESS ===");
    
    if (!"trainer".equals(session.getAttribute("userrole"))) {
        System.out.println("❌ Not authenticated as trainer");
        return "redirect:/login";
    }
    
    Long trainerId = (Long) session.getAttribute("trainerId");
    System.out.println("Trainer ID from session: " + trainerId);
    
    if (trainerId == null) {
        System.err.println("❌ Trainer ID is null!");
        session.invalidate();
        return "redirect:/login";
    }
    
    try {
        List<WorkoutPlan> plans = workoutPlanDAO.findByTrainer(trainerId);
        System.out.println("✅ Found " + plans.size() + " workout plans");

        if (keyword != null && !keyword.isEmpty()) {
            String search = keyword.toLowerCase(); // Convert to lowercase for case-insensitive search
            
            // Filter the list using Java Streams
            plans = plans.stream()
                .filter(p -> 
                    (p.getName() != null && p.getName().toLowerCase().contains(search)) ||
                    (p.getMember() != null && p.getMember().getName().toLowerCase().contains(search)) ||
                    (p.getGoal() != null && p.getGoal().toLowerCase().contains(search))
                )
                .collect(java.util.stream.Collectors.toList());
            
            // Add the keyword back to the model so it stays in the search box
            model.addAttribute("keyword", keyword);
        }
        
        for (WorkoutPlan plan : plans) {
            if (plan.getMember() != null) {
                org.hibernate.Hibernate.initialize(plan.getMember());
            }
            if (plan.getTrainer() != null) {
                org.hibernate.Hibernate.initialize(plan.getTrainer());
            }
        }
        
        model.addAttribute("plans", plans);
    } catch (Exception e) {
        System.err.println("❌ Error loading plans: " + e.getMessage());
        e.printStackTrace();
        model.addAttribute("plans", List.of());
    }
    
    return "trainer/plan-list";
}


    @GetMapping("/plans/view/{id}")
    public String viewPlan(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!"trainer".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }
        
        try {
            WorkoutPlan plan = workoutPlanDAO.findById(id);
            
            if (plan == null) {
                System.out.println("⚠️ Plan not found with ID: " + id);
                return "redirect:/trainer/plans";
            }
            
            model.addAttribute("plan", plan);
            return "trainer/plan-view";
        } catch (Exception e) {
            System.err.println("❌ Error loading plan: " + e.getMessage());
            return "redirect:/trainer/plans";
        }
    }


    @GetMapping("/plans/new")
    public String showCreatePlanForm(Model model, HttpSession session) {
        if (!"trainer".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }
        
        WorkoutPlan plan = new WorkoutPlan();
        
        try {
            List<Member> members = memberDAO.findAll();
            model.addAttribute("members", members);
        } catch (Exception e) {
            System.out.println("⚠️ Could not load members from DB: " + e.getMessage());
            model.addAttribute("members", List.of()); // Empty list
        }
        
        model.addAttribute("workoutPlan", plan);
        return "trainer/plan-create"; 
    }


    @GetMapping("/plans/edit/{id}")
    public String showEditPlanForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!"trainer".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }

        try {
            WorkoutPlan plan = workoutPlanDAO.findById(id);
            
            if (plan == null) {
                return "redirect:/trainer/plans";
            }
            
            List<Member> members = memberDAO.findAll();
            
            model.addAttribute("workoutPlan", plan);
            model.addAttribute("members", members);
            
            return "trainer/plan-create";
        } catch (Exception e) {
            System.err.println("❌ Error loading plan for edit: " + e.getMessage());
            return "redirect:/trainer/plans";
        }
    }

    @PostMapping("/plans/update")
    public String updateWorkoutPlan(
        @Valid @ModelAttribute("workoutPlan") WorkoutPlan workoutPlan,
        BindingResult result,
        @RequestParam("personId") long personId,
        Model model,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        if (!"trainer".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            System.out.println("⚠️ Validation errors found during update");
            try {
                List<Member> members = memberDAO.findAll();
                model.addAttribute("members", members);
            } catch (Exception e) {
                model.addAttribute("members", List.of());
            }
            return "trainer/plan-create"; 
        }

        try {
            Member assignedMember = memberDAO.findById(personId);
            if (assignedMember == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected member not found.");
                return "redirect:/trainer/plans";
            }
            workoutPlan.setMember(assignedMember);

            Long trainerId = (Long) session.getAttribute("trainerId");
            if (trainerId == null) {
                return "redirect:/login";
            }
            
            Trainer currentTrainer = trainerDAO.findById(trainerId);
            if (currentTrainer == null) {
                System.err.println("❌ Trainer not found in database!");
                return "redirect:/login";
            }
            workoutPlan.setTrainer(currentTrainer);

            workoutPlanDAO.update(workoutPlan); 

            redirectAttributes.addFlashAttribute("successMessage", "Workout Plan updated successfully!");
            System.out.println("✅ Workout plan updated: " + workoutPlan.getName());
            
        } catch (Exception e) {
            System.err.println("❌ Error updating workout plan: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update workout plan.");
        }
        
        return "redirect:/trainer/plans";
    }

    @PostMapping("/plans/delete")
    public String deleteWorkoutPlan(@RequestParam("id") Long id, 
                                   HttpSession session, 
                                   RedirectAttributes redirectAttributes) {
        if (!"trainer".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }

        try {
            workoutPlanDAO.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Workout Plan deleted successfully.");
            System.out.println("✅ Workout plan deleted with ID: " + id);
        } catch (Exception e) {
            System.err.println("❌ Error deleting workout plan: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete workout plan.");
        }
        
        return "redirect:/trainer/plans";
    }

    @PostMapping("/plans/save")
    public String saveWorkoutPlan(
        @Valid @ModelAttribute("workoutPlan") WorkoutPlan workoutPlan,
        BindingResult result,
        @RequestParam("personId") long personId,
        Model model,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        if (!"trainer".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            System.out.println("--------------------------------------------------");
            System.out.println("❌ VALIDATION FAILED! HERE IS THE REASON:");
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            System.out.println("--------------------------------------------------");
            
            try {
                List<Member> members = memberDAO.findAll();
                model.addAttribute("members", members);
            } catch (Exception e) {
                model.addAttribute("members", List.of());
            }
            return "trainer/plan-create";
        }

        try {
            Member assignedMember = memberDAO.findById(personId);
            if (assignedMember == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected member not found.");
                return "redirect:/trainer/plans";
            }
            workoutPlan.setMember(assignedMember);
            
            Long trainerId = (Long) session.getAttribute("trainerId");
            
            if (trainerId == null) {
                System.err.println("❌ ERROR: Trainer ID is null in session during save!");
                return "redirect:/login";
            }
            
            Trainer currentTrainer = trainerDAO.findById(trainerId);
            
            if (currentTrainer == null) {
                System.err.println("❌ ERROR: Could not find trainer with ID: " + trainerId);
                return "redirect:/login";
            }
            
            workoutPlan.setTrainer(currentTrainer); 
            
            workoutPlanDAO.save(workoutPlan);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Workout Plan '" + workoutPlan.getName() + "' created successfully.");
            
            System.out.println("✅ Workout plan created: " + workoutPlan.getName());
            
        } catch (Exception e) {
            System.err.println("❌ Error saving workout plan: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create workout plan.");
        }
        
        return "redirect:/trainer/plans";
    }
}