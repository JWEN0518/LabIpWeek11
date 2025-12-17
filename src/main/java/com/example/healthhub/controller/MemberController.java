package com.example.healthhub.controller;

import com.example.healthhub.dao.ProgramDAO;
import com.example.healthhub.dao.BmiRecordDAO;
import com.example.healthhub.dao.EnrollmentDAO;
import com.example.healthhub.dao.MemberDAO;
import com.example.healthhub.dao.WorkoutPlanDAO;
import com.example.healthhub.model.Program;
import com.example.healthhub.model.BmiRecord;
import com.example.healthhub.model.Enrollment;
import com.example.healthhub.model.Member;
import com.example.healthhub.model.WorkoutPlan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {
    
    @Autowired
    private ProgramDAO programDAO;
    
    @Autowired
    private EnrollmentDAO enrollmentDAO;
    
    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private WorkoutPlanDAO workoutPlanDAO;

    @Autowired
    private BmiRecordDAO bmiRecordDAO;

    @GetMapping("/dashboard")
    public String memberDashboard(Model model, HttpSession session) {
        String role = (String) session.getAttribute("userrole");
        if (!"member".equals(role)) {
            return "redirect:/login";
        }
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            Member member = memberDAO.findById(userId);
            
            List<Enrollment> enrollments = enrollmentDAO.findByMember(userId);
            long activeEnrollments = enrollments.stream()
                .filter(e -> "Active".equals(e.getStatus()))
                .count();
            long pendingPayments = enrollments.stream()
                .filter(e -> "Pending".equals(e.getPaymentStatus()))
                .count();
            
            List<WorkoutPlan> plans = workoutPlanDAO.findByMember(userId);
            
            WorkoutPlan activePlan = plans.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive())) 
                .findFirst()
                .orElse(null);
            
            long totalPrograms = programDAO.findActivePrograms().size();
            
            model.addAttribute("member", member);
            model.addAttribute("activeEnrollments", activeEnrollments);
            model.addAttribute("pendingPayments", pendingPayments);
            model.addAttribute("activePlan", activePlan);
            model.addAttribute("totalPrograms", totalPrograms);
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            model.addAttribute("activeEnrollments", 0L);
            model.addAttribute("pendingPayments", 0L);
            model.addAttribute("activePlan", null);
            model.addAttribute("totalPrograms", 0L);
        }

        List<BmiRecord> bmiHistory = bmiRecordDAO.findByMember(userId);
        if (!bmiHistory.isEmpty()) {
            BmiRecord latestBmi = bmiHistory.get(0);
            model.addAttribute("latestBmi", latestBmi);
            model.addAttribute("bmiRecordCount", bmiHistory.size());
        }
        
        return "member/dashboard";
    }

    @GetMapping("/programs")
    public String listPrograms(Model model, HttpSession session) {
        if (!"member".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }
        
        try {
            List<Program> programs = programDAO.findActivePrograms();
            model.addAttribute("programs", programs);
        } catch (Exception e) {
            System.out.println("⚠️ Could not load programs from DB");
            model.addAttribute("programs", List.of());
        }
        
        return "member/program-list";
    }
    
    @GetMapping("/enrollments")
    public String myEnrollments(Model model, HttpSession session) {
        if (!"member".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        try {
            List<Enrollment> enrollments = enrollmentDAO.findByMember(userId);
            model.addAttribute("enrollments", enrollments);
        } catch (Exception e) {
            System.out.println("⚠️ Could not load enrollments from DB");
            model.addAttribute("enrollments", List.of());
        }
        
        return "member/my-enrollments";
    }

    @GetMapping("/enroll-form")
    public String showEnrollmentForm(Model model, HttpSession session) {
        if (!"member".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }
        
        try {
            List<Program> programs = programDAO.findActivePrograms();
            model.addAttribute("programs", programs);
        } catch (Exception e) {
            System.out.println("⚠️ Could not load programs from DB");
            model.addAttribute("programs", List.of());
        }
        
        model.addAttribute("enrollment", new Enrollment());
        return "member/enroll-form";
    }

    @PostMapping("/enroll")
    public String enrollInProgram(@RequestParam("programId") Long programId, 
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!"member".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        try {
            Member member = memberDAO.findById(userId);
            Program program = programDAO.findById(programId);
            
            if (member == null || program == null) {
                redirectAttributes.addFlashAttribute("error", "Error: Invalid enrollment data.");
                return "redirect:/member/enroll-form";
            }
            
            Enrollment newEnrollment = new Enrollment(member, program);
            newEnrollment.setStartDate(newEnrollment.getEnrollmentDate().plusDays(7));
            enrollmentDAO.save(newEnrollment);
            
            redirectAttributes.addFlashAttribute("success", 
                "Successfully enrolled in " + program.getName() + "!");
        } catch (Exception e) {
            System.out.println("⚠️ Enrollment failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Enrollment failed. Please try again.");
        }
        
        return "redirect:/member/enrollments";
    }

    @GetMapping("/workout-plans")
    public String workoutPlans(Model model, HttpSession session) {
        if (!"member".equals(session.getAttribute("userrole"))) {
            return "redirect:/login";
        }
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        try {
            List<WorkoutPlan> memberPlans = workoutPlanDAO.findByMember(userId);
            
            WorkoutPlan currentPlan = memberPlans.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .findFirst()
                .orElse(null);
                
            model.addAttribute("currentPlan", currentPlan);
            model.addAttribute("allPlans", memberPlans);
        } catch (Exception e) {
            System.out.println("⚠️ Could not load workout plans from DB");
            model.addAttribute("currentPlan", null);
            model.addAttribute("allPlans", List.of());
        }
        
        return "member/workout-plans";
    }
}