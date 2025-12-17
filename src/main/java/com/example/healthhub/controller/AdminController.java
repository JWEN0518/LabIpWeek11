package com.example.healthhub.controller;

import com.example.healthhub.dao.EnrollmentDAO;
import com.example.healthhub.dao.MemberDAO;
import com.example.healthhub.dao.ProgramDAO;
import com.example.healthhub.dao.BmiRecordDAO;
import com.example.healthhub.model.Member;
import com.example.healthhub.model.Program;
import com.example.healthhub.model.BmiRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private BmiRecordDAO bmiRecordDAO;

    @Autowired 
    private EnrollmentDAO enrollmentDAO;

    @Autowired 
    private ProgramDAO programDAO;

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("userrole");
        return "admin".equals(role);
    }

    
    private String checkRoleAndRedirect(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login"; 
        }
        return null; 
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/person/list")
    public String listPersons(@RequestParam(required = false) String searchName, 
                         Model model, 
                         HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
    
        try {
            List<Member> members = memberDAO.findAll();

            if (searchName != null && !searchName.trim().isEmpty()) {
                String q = searchName.trim().toLowerCase();
                members = members.stream()
                        .filter(m -> m.getName() != null && m.getName().toLowerCase().contains(q))
                        .collect(java.util.stream.Collectors.toList());
            }

            for (Member m : members) {
                org.hibernate.Hibernate.initialize(m.getBmiHistory());
            }

            model.addAttribute("persons", members);
        } catch (Exception e) {
            System.err.println("❌ Error loading members list: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("persons", java.util.List.of());
            model.addAttribute("error", "Failed to load members. Please try again.");
        }
        return "admin/person-list";
    }

    @GetMapping("/person/view/{id}")
    public String viewPerson(@PathVariable("id") Long id, Model model, HttpSession session) {
        System.out.println("=== ADMIN VIEW PERSON ===");
        System.out.println("Viewing member ID: " + id);
        
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            Member member = memberDAO.findById(id);
            
            if (member == null) {
                System.err.println("❌ Member not found with ID: " + id);
                return "redirect:/admin/person/list";
            }
            
            System.out.println("✅ Member loaded: " + member.getName());
            
            List<BmiRecord> history = bmiRecordDAO.findByMember(id);
            System.out.println("✅ Found " + history.size() + " BMI records");
            
            model.addAttribute("person", member);
            model.addAttribute("bmiHistory", history);
            
            return "admin/person-view";
            
        } catch (Exception e) {
            System.err.println("❌ Error loading member: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/person/list";
        }
    }

    @GetMapping("/person/new")
public String showCreateMemberForm(Model model, HttpSession session) {
    if (!isAdmin(session)) {
        return "redirect:/login";
    }
    
    Member newMember = new Member();
    model.addAttribute("member", newMember);
    model.addAttribute("isNewMember", true); 
    
    return "admin/person-form"; 
}

@PostMapping("/person/create")
public String createMember(@ModelAttribute Member memberData,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
    if (!isAdmin(session)) {
        return "redirect:/login";
    }
    
    try {
        memberData.setIsActive(true);
        
        memberDAO.save(memberData);
        
        redirectAttributes.addFlashAttribute("flashMessage", 
            "Successfully created new member: " + memberData.getName());
        
        return "redirect:/admin/person/list";
        
    } catch (Exception e) {
        System.err.println("❌ Error creating member: " + e.getMessage());
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("errorMessage", 
            "Failed to create member. Please check if username/email already exists.");
        return "redirect:/admin/person/new";
    }
}

@GetMapping("/person/edit/{id}")
public String showEditForm(@PathVariable("id") Long id, Model model, HttpSession session) {
    if (!isAdmin(session)) {
        return "redirect:/login";
    }
    
    Member member = memberDAO.findById(id);
    if (member == null) {
        return "redirect:/admin/person/list";
    }
    
    model.addAttribute("member", member);
    return "admin/person-edit";  
}

@PostMapping("/person/update")
public String updatePerson(@RequestParam("id") Long id,
                          @ModelAttribute Member memberData, 
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
    if (!isAdmin(session)) {
        return "redirect:/login";
    }
    
    Member existing = memberDAO.findById(id);
    if (existing == null) {
        redirectAttributes.addFlashAttribute("flashMessage", "Error: Member not found.");
        return "redirect:/admin/person/list";
    }
    
    existing.setName(memberData.getName());
    existing.setEmail(memberData.getEmail());
    existing.setPhone(memberData.getPhone());
    existing.setMemberType(memberData.getMemberType());
    existing.setYearOfBirth(memberData.getYearOfBirth());
    existing.setGender(memberData.getGender());
    
    memberDAO.update(existing);
    
    redirectAttributes.addFlashAttribute("flashMessage", "Successfully updated member: " + existing.getName());
    return "redirect:/admin/person/list";
}

    @PostMapping("/person/delete")
public String deleteMember(@RequestParam("id") Long id, 
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
    if (!isAdmin(session)) {
        return "redirect:/login";
    }
    
    try {
        Member member = memberDAO.findById(id);
        
        if (member == null) {
            redirectAttributes.addFlashAttribute("flashMessage", 
                "Error: Member not found.");
            return "redirect:/admin/person/list";
        }
        
        String memberName = member.getName();
        
        memberDAO.delete(id);
        
        redirectAttributes.addFlashAttribute("flashMessage", 
            "Successfully deleted member: " + memberName + " and all associated records.");
        
    } catch (Exception e) {
        System.err.println("❌ Error deleting member: " + e.getMessage());
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("errorMessage", 
            "Failed to delete member. They may have dependent records.");
    }
    
    return "redirect:/admin/person/list";
}

    @GetMapping("/reports")
    public String viewReports(HttpSession session, Model model) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        Long totalPrograms = (long) programDAO.findAll().size(); 
        Long totalUsers = (long) memberDAO.findAll().size();
        Long totalEnrollments = enrollmentDAO.countAllEnrollments(); 


        List<Object[]> rawReports = enrollmentDAO.findTopEnrolledPrograms(5);
        
        List<Program> popularPrograms = rawReports.stream()
            .map(obj -> (Program) obj[0])
            .collect(Collectors.toList());
        
        Map<Long, Long> enrollmentCounts = rawReports.stream()
            .collect(Collectors.toMap(
                obj -> ((Program) obj[0]).getId(), 
                obj -> (Long) obj[1]              
            ));

        model.addAttribute("totalPrograms", totalPrograms);
        model.addAttribute("totalEnrollments", totalEnrollments);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("popularPrograms", popularPrograms);
        model.addAttribute("enrollmentCounts", enrollmentCounts); 
        
        model.addAttribute("pageTitle", "System Summary & Enrollment Reports");
        return "admin/reports"; 
    }
}