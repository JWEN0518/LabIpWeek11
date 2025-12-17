package com.example.healthhub.dao;

import com.example.healthhub.model.Enrollment;
import java.util.List;

public interface EnrollmentDAO extends GenericDAO<Enrollment> {
    List<Enrollment> findByProgram(Long programId);
    List<Enrollment> findActiveEnrollments();
    Long countAllEnrollments();
    List<Object[]> findTopEnrolledPrograms(int limit);
    
    List<Enrollment> findByMember(Long memberId);
}