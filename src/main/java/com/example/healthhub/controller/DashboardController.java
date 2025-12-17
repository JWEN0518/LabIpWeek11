package com.example.healthhub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    /**
     * Handles requests for the /dashboard URL.
     * This is the primary mapping for the application's main page after login.
     * @return The name of the view (dashboard.html, assuming you are using Thymeleaf or a similar view resolver).
     */
    
    @GetMapping("/dashboard")
    public String viewDashboard() {
        // This method simply returns the view name "dashboard".
        // Spring MVC will look for a template file named 'dashboard' (e.g., dashboard.html or dashboard.jsp)
        // in the configured view resolver location (e.g., /WEB-INF/views/).
        return "dashboard";
    }
    
}
