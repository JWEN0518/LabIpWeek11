package com.example.healthhub.controller;

import com.example.healthhub.dao.CategoryDAO;
import com.example.healthhub.dao.ProgramDAO;
import com.example.healthhub.dao.TrainerDAO;
import com.example.healthhub.model.Program;
import com.example.healthhub.model.Category;
import com.example.healthhub.model.Trainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("/admin/programs")
public class ProgramController {

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private CategoryDAO categoryDAO; 

    @Autowired
    private TrainerDAO trainerDAO;

 
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

    @GetMapping("/list")
    public String listPrograms(HttpSession session, Model model) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        List<Program> programs = programDAO.findAll();
        model.addAttribute("programs", programs);
        model.addAttribute("pageTitle", "Manage Fitness Programs");
        model.addAttribute("isFilteredView", false);
        return "admin/program-list"; 
    }

    @GetMapping({"/new", "/edit/{id}"})
    public String showForm(@PathVariable(required = false) Long id, HttpSession session, Model model) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        Program program = (id == null) ? new Program() : programDAO.findById(id);
        
        List<Category> categories = categoryDAO.findAll();
        List<Trainer> trainers = trainerDAO.findAll();
        
        model.addAttribute("program", program);
        model.addAttribute("categories", categories);
        model.addAttribute("trainers", trainers);
        model.addAttribute("pageTitle", (id == null) ? "Add New Program" : "Edit Program");
        
        return "admin/program-form"; 
    }

    @PostMapping("/save")
    public String saveProgram(@ModelAttribute Program program, 
                              @RequestParam("categoryId") Long categoryId, 
                              HttpSession session, 
                              RedirectAttributes redirectAttributes) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        Category category = categoryDAO.findById(categoryId);
        program.setCategory(category);
        
        if (program.getId() == null) {
            programDAO.save(program);
            redirectAttributes.addFlashAttribute("flashMessage", "Program created successfully!");
        } else {
            programDAO.update(program);
            redirectAttributes.addFlashAttribute("flashMessage", "Program updated successfully!");
        }
        
        return "redirect:/admin/programs/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteProgram(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        Program program = programDAO.findById(id);
        if (program != null) {
            programDAO.delete(id);
            redirectAttributes.addFlashAttribute("flashMessage", 
                "Program '" + program.getName() + "' deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("flashMessage", "Error: Program not found.");
        }
        
        return "redirect:/admin/programs/list";
    }

    @GetMapping("/viewByCategory/{categoryId}")
    public String viewProgramsByCategory(@PathVariable("categoryId") Long categoryId, HttpSession session, Model model) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        List<Program> programs = programDAO.findByCategory(categoryId);
    
        Category category = categoryDAO.findById(categoryId); 

        model.addAttribute("programs", programs);
        model.addAttribute("pageTitle", "Programs in Category: " + category.getName());
        model.addAttribute("isFilteredView", true); 
    
        return "admin/program-list";
}

    @GetMapping("/view/{id}")
    public String viewProgram(@PathVariable("id") Long id, HttpSession session, Model model) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        Program program = programDAO.findById(id);
        
        if (program == null) {
            model.addAttribute("errorMessage", "Program not found.");
            return "redirect:/admin/programs/list"; 
        }

        model.addAttribute("program", program);
        model.addAttribute("pageTitle", "Program Details: " + program.getName());
        
        return "admin/program-view"; 
    }

}