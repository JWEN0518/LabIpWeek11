package com.example.healthhub.dao;

import com.example.healthhub.model.Trainer;
import java.util.List;

public interface TrainerDAO extends GenericDAO<Trainer> {
    Trainer findByUsername(String username);
    List<Trainer> findActiveTrainers();
    Trainer authenticate(String username, String password);
}