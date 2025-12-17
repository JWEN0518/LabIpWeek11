package com.example.healthhub.dao;

import com.example.healthhub.model.Category;

public interface CategoryDAO extends GenericDAO<Category> {
    Category findByName(String name);
}