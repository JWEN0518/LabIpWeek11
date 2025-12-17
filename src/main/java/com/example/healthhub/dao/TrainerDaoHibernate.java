package com.example.healthhub.dao;

import com.example.healthhub.model.Trainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TrainerDaoHibernate implements TrainerDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Trainer trainer) {
        getSession().save(trainer);
    }

    @Override
    public void update(Trainer trainer) {
        getSession().update(trainer);
    }

    @Override
    public void delete(Long id) {
        Trainer trainer = findById(id);
        if (trainer != null) {
            getSession().delete(trainer);
        }
    }

    @Override
    public Trainer findById(Long id) {
        return getSession().get(Trainer.class, id);
    }

    @Override
    public List<Trainer> findAll() {
        Query<Trainer> query = getSession().createQuery("FROM Trainer", Trainer.class);
        return query.getResultList();
    }

    @Override
    public Trainer findByUsername(String username) {
        Query<Trainer> query = getSession()
            .createQuery("FROM Trainer WHERE username = :username", Trainer.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    @Override
    public List<Trainer> findActiveTrainers() {
        Query<Trainer> query = getSession()
            .createQuery("FROM Trainer WHERE isActive = true", Trainer.class);
        return query.getResultList();
    }

    @Override
    public Trainer authenticate(String username, String password) {
        Query<Trainer> query = getSession()
            .createQuery("FROM Trainer WHERE username = :username AND password = :password", 
                        Trainer.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.uniqueResult();
    }
}