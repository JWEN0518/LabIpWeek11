package com.example.healthhub.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.example.healthhub.dao.BmiRecordDAO;
import com.example.healthhub.dao.MemberDAO;
import com.example.healthhub.model.BmiRecord;
import com.example.healthhub.model.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/bmi")
public class BmiController {

    @Autowired
    private BmiRecordDAO bmiRecordDAO;

    @Autowired
    private MemberDAO memberDAO;
    
    public BmiController() {
        System.out.println("========================================");
        System.out.println("✅ BmiController INITIALIZED!");
        System.out.println("========================================");
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String showForm(Model model, HttpSession session) {
        System.out.println("=== BMI FORM GET REQUEST ===");
        
        String role = (String) session.getAttribute("userrole");
        Long userId = (Long) session.getAttribute("userId");
        
        if (!"member".equals(role) || userId == null) {
            System.out.println("❌ Access denied - not a member");
            model.addAttribute("errorMessage", "Only members can access the BMI calculator.");
            return "redirect:/login";
        }
        
        BmiRecord emptyRecord = new BmiRecord();
        
        try {
            Member member = memberDAO.findById(userId);
            if (member != null) {
                emptyRecord.setName(member.getName());
                emptyRecord.setGender(member.getGender());
                emptyRecord.setYearOfBirth(member.getYearOfBirth());
                System.out.println("✅ Pre-filled form for: " + member.getName());
            }
        } catch (Exception e) {
            System.err.println("Could not pre-fill member info: " + e.getMessage());
        }
        
        model.addAttribute("bmiRecord", emptyRecord);
        return "bmi-form";
    }

    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    public String calculateBmi(@ModelAttribute("bmiRecord") @Valid BmiRecord bmiRecord,
                              BindingResult bindingResult,
                              @RequestParam(value = "interests", required = false) String[] interests,
                              HttpSession session,
                              Model model) {
        
        System.out.println("========================================");
        System.out.println("=== BMI CALCULATE POST REQUEST RECEIVED ===");
        System.out.println("Height: " + (bmiRecord.getHeight() != null ? bmiRecord.getHeight() : "NULL"));
        System.out.println("Weight: " + (bmiRecord.getWeight() != null ? bmiRecord.getWeight() : "NULL"));
        System.out.println("========================================");
        
        String role = (String) session.getAttribute("userrole");
        Long userId = (Long) session.getAttribute("userId");
        
        if (!"member".equals(role) || userId == null) {
            System.out.println("❌ Access denied - not a member");
            model.addAttribute("errorMessage", "Only members can calculate BMI.");
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            System.out.println("❌ Validation errors found!");
            bindingResult.getAllErrors().forEach(error -> 
                System.out.println("Error: " + error.toString())
            );
            return "bmi-form";
        }

        bmiRecord.setInterestsArray(interests);
        
        try {
            Member member = memberDAO.findById(userId);
            if (member != null) {
                bmiRecord.setMember(member);
                System.out.println("✅ BMI record linked to member: " + member.getName());
            } else {
                System.err.println("❌ Member not found with ID: " + userId);
                model.addAttribute("errorMessage", "Member account not found.");
                return "bmi-form";
            }
        } catch (Exception e) {
            System.err.println("❌ Error linking BMI to member: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error processing BMI calculation.");
            return "bmi-form";
        }
        
        try {
            bmiRecordDAO.save(bmiRecord);
            System.out.println("✅ BMI record saved! ID: " + bmiRecord.getId());
            model.addAttribute("message", "BMI calculated and saved successfully!");
        } catch (Exception e) {
            System.err.println("❌ ERROR saving BMI record: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error saving data: " + e.getMessage());
            return "bmi-form";
        }

        System.out.println("=== BMI CALCULATE COMPLETE - REDIRECTING TO RESULT ===");
        
        model.addAttribute("bmiRecord", bmiRecord);
        return "bmi-result";
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String viewHistory(Model model, HttpSession session) {
        String role = (String) session.getAttribute("userrole");
        Long userId = (Long) session.getAttribute("userId");
        
        if (!"member".equals(role) || userId == null) {
            return "redirect:/login";
        }
        
        try {
            List<BmiRecord> history = bmiRecordDAO.findByMember(userId);
            model.addAttribute("bmiHistory", history);
            
            Member member = memberDAO.findById(userId);
            model.addAttribute("member", member);
            
            System.out.println("✅ Loaded " + history.size() + " BMI records for member " + userId);
        } catch (Exception e) {
            System.err.println("❌ Error loading BMI history: " + e.getMessage());
            model.addAttribute("bmiHistory", List.of());
        }
        
        return "member/bmi-history";
    }

}