package com.example.healthhub.dao;

import com.example.healthhub.model.WorkoutPlan;
import java.util.List;

public interface WorkoutPlanDAO extends GenericDAO<WorkoutPlan> {
    List<WorkoutPlan> findByTrainer(Long trainerId);
    List<WorkoutPlan> findActivePlans();
    List<WorkoutPlan> getPlansByTrainer(Long trainerId);
    WorkoutPlan createNewPlan(WorkoutPlan plan);
    long countByTrainer(Long trainerId);
    
    List<WorkoutPlan> findByMember(Long memberId);
}