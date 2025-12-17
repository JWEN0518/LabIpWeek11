package com.example.healthhub.controller;

import com.example.healthhub.dao.CategoryDAO;
import com.example.healthhub.model.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryDAO categoryDAO;

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
    public String listCategories(HttpSession session, Model model) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        List<Category> categories = categoryDAO.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Manage Categories");
        return "admin/category-list";
    }

    @GetMapping({"/new", "/edit/{id}"})
    public String showForm(@PathVariable(required = false) Long id, HttpSession session, Model model) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        Category category = (id == null) ? new Category() : categoryDAO.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", (id == null) ? "Add New Category" : "Edit Category");
        return "admin/category-form";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        if (category.getId() == null) {
            categoryDAO.save(category);
            redirectAttributes.addFlashAttribute("flashMessage", "Category created successfully!");
        } else {
            categoryDAO.update(category);
            redirectAttributes.addFlashAttribute("flashMessage", "Category updated successfully!");
        }

        return "redirect:/admin/categories/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = checkRoleAndRedirect(session);
        if (redirect != null) return redirect;

        Category category = categoryDAO.findById(id);
        if (category != null) {
            categoryDAO.delete(id);
            redirectAttributes.addFlashAttribute("flashMessage", "Category '" + category.getName() + "' deleted.");
        } else {
            redirectAttributes.addFlashAttribute("flashMessage", "Category not found.");
        }

        return "redirect:/admin/categories/list";
    }

}
