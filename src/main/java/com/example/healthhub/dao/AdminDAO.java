package com.example.healthhub.dao;

import com.example.healthhub.model.Admin;

public interface AdminDAO extends GenericDAO<Admin> {
    Admin findByUsername(String username);
    Admin authenticate(String username, String password);
}

