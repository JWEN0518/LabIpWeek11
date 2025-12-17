package com.example.healthhub.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.healthhub.dao.MemberDAO;
import com.example.healthhub.dao.TrainerDAO;
import com.example.healthhub.dao.AdminDAO;
import com.example.healthhub.model.Member;
import com.example.healthhub.model.Trainer;
import com.example.healthhub.model.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private TrainerDAO trainerDAO;

    @Autowired
    private AdminDAO adminDAO;

    @GetMapping("/")
    public String landing() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                               @RequestParam String password, 
                               HttpServletRequest request,
                               Model model) {
        
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Username: " + username);
        
        HttpSession session = request.getSession(true);
        
        session.removeAttribute("username");
        session.removeAttribute("userrole");
        session.removeAttribute("userId");
        session.removeAttribute("trainerId");
        
        try {
            Member member = memberDAO.authenticate(username, password);
            if (member != null && member.getIsActive()) {
                session.setAttribute("username", member.getName());
                session.setAttribute("userrole", "member");
                session.setAttribute("userId", member.getId());
                System.out.println("✅ Member login successful: " + member.getName());
                return "redirect:/member/dashboard";
            }
            
            Trainer trainer = trainerDAO.authenticate(username, password);
            if (trainer != null && trainer.getIsActive()) {
                session.setAttribute("username", trainer.getName());
                session.setAttribute("userrole", "trainer");
                session.setAttribute("userId", trainer.getId());
                session.setAttribute("trainerId", trainer.getId());
                System.out.println("✅ Trainer login successful: " + trainer.getName());
                return "redirect:/trainer/dashboard";
            }
            
            Admin admin = adminDAO.authenticate(username, password);
            if (admin != null && admin.getIsActive()) {
                session.setAttribute("username", admin.getName());
                session.setAttribute("userrole", "admin");
                session.setAttribute("userId", admin.getId());
                System.out.println("✅ Admin login successful: " + admin.getName());
                return "redirect:/admin/dashboard";
            }
            
            System.out.println("❌ Login failed - invalid credentials");
            model.addAttribute("errorMessage", "Invalid username or password.");
            return "login";
            
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Login failed. Please try again.");
            return "login";
        }
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        System.out.println("=== /home accessed ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Username in session: " + session.getAttribute("username"));
        System.out.println("Role in session: " + session.getAttribute("userrole"));
        
        if (session.getAttribute("username") == null) {
            System.out.println("No username found, redirecting to login");
            return "redirect:/login";
        }
        
        String role = (String) session.getAttribute("userrole");
        if ("admin".equals(role)) {
            return "redirect:/admin/dashboard";
        } else if ("trainer".equals(role)) {
            return "redirect:/trainer/dashboard";
        } else if ("member".equals(role)) {
            return "redirect:/member/dashboard";
        }
        
        return "index";
    }

    @GetMapping("/register")
public String showRegisterForm(Model model, HttpSession session) {
    if (session.getAttribute("username") != null) {
        return "redirect:/home";
    }
    
    model.addAttribute("member", new Member());
    return "register";
}

@PostMapping("/register")
public String processRegistration(@ModelAttribute Member memberData,
                                 BindingResult result,
                                 Model model,
                                 HttpServletRequest request) {
    
    System.out.println("=== REGISTRATION ATTEMPT ===");
    System.out.println("Username: " + memberData.getUsername());
    
    if (memberData.getName() == null || memberData.getName().trim().isEmpty()) {
        model.addAttribute("errorMessage", "Name is required.");
        return "register";
    }
    
    if (memberData.getUsername() == null || memberData.getUsername().trim().isEmpty()) {
        model.addAttribute("errorMessage", "Username is required.");
        return "register";
    }
    
    if (memberData.getPassword() == null || memberData.getPassword().trim().isEmpty()) {
        model.addAttribute("errorMessage", "Password is required.");
        return "register";
    }
    
    if (memberData.getEmail() == null || memberData.getEmail().trim().isEmpty()) {
        model.addAttribute("errorMessage", "Email is required.");
        return "register";
    }
    
    try {
        Member existingUser = memberDAO.findByUsername(memberData.getUsername());
        if (existingUser != null) {
            model.addAttribute("errorMessage", "Username already taken. Please choose another.");
            model.addAttribute("member", memberData);
            return "register";
        }
        
        memberData.setIsActive(true);
        
        memberDAO.save(memberData);
        
        System.out.println("✅ Registration successful: " + memberData.getName());
        
        HttpSession session = request.getSession(true);
        session.setAttribute("username", memberData.getName());
        session.setAttribute("userrole", "member");
        session.setAttribute("userId", memberData.getId());
        
        return "redirect:/member/dashboard";
        
    } catch (Exception e) {
        System.err.println("❌ Registration error: " + e.getMessage());
        e.printStackTrace();
        model.addAttribute("errorMessage", "Registration failed. Username or email may already exist.");
        model.addAttribute("member", memberData);
        return "register";
    }
}

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("=== LOGOUT ===");
        System.out.println("Invalidating session: " + session.getId());
        session.invalidate();
        return "redirect:/login?logoutMsg=You have successfully logged out.";
    }
}