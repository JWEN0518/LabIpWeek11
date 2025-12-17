package com.example.healthhub.dao;

import com.example.healthhub.model.Program;
import java.util.List;

public interface ProgramDAO extends GenericDAO<Program> {
    List<Program> findActivePrograms();
    List<Program> findByCategory(Long categoryId);
    List<Program> searchByName(String name);
}