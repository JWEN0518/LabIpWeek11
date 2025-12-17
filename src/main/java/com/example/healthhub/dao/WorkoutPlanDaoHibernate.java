package com.example.healthhub.dao;

import com.example.healthhub.model.WorkoutPlan;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class WorkoutPlanDaoHibernate implements WorkoutPlanDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(WorkoutPlan plan) {
        getSession().save(plan);
    }

    @Override
    public void update(WorkoutPlan plan) {
        getSession().update(plan);
    }

    @Override
    public void delete(Long id) {
        WorkoutPlan plan = findById(id);
        if (plan != null) {
            getSession().delete(plan);
        }
    }

    @Override
    public WorkoutPlan findById(Long id) {
        return getSession().get(WorkoutPlan.class, id);
    }

    @Override
    public List<WorkoutPlan> findAll() {
        Query<WorkoutPlan> query = getSession().createQuery("FROM WorkoutPlan", WorkoutPlan.class);
        return query.getResultList();
    }


    @Override
    public List<WorkoutPlan> findByTrainer(Long trainerId) {
        Query<WorkoutPlan> query = getSession()
            .createQuery("FROM WorkoutPlan w WHERE w.trainer.id = :trainerId", WorkoutPlan.class);
        query.setParameter("trainerId", trainerId);
        return query.getResultList();
    }

    @Override
    public List<WorkoutPlan> findActivePlans() {
        Query<WorkoutPlan> query = getSession()
            .createQuery("FROM WorkoutPlan w WHERE w.isActive = true", WorkoutPlan.class);
        return query.getResultList();
    }

    @Override
    public List<WorkoutPlan> getPlansByTrainer(Long trainerId) {
        return findByTrainer(trainerId);
    }

    @Override
    @Transactional
    public WorkoutPlan createNewPlan(WorkoutPlan plan) {
        save(plan); 
        return plan; 
    }

    @Override
    public long countByTrainer(Long trainerId) {
        String hql = "SELECT COUNT(w) FROM WorkoutPlan w WHERE w.trainer.id = :tid";
        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("tid", trainerId)
                .uniqueResult();
    }

    @Override
    public List<WorkoutPlan> findByMember(Long memberId) {
        Query<WorkoutPlan> query = getSession()
            .createQuery("FROM WorkoutPlan w WHERE w.member.id = :memberId", WorkoutPlan.class);
        query.setParameter("memberId", memberId);
        return query.getResultList();
    }
    
}