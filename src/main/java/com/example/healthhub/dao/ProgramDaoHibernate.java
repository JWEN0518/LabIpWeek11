package com.example.healthhub.dao;

import com.example.healthhub.model.Program;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ProgramDaoHibernate implements ProgramDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Program program) {
        getSession().save(program);
    }

    @Override
    public void update(Program program) {
        getSession().update(program);
    }

    @Override
    public void delete(Long id) {
        Program program = findById(id);
        if (program != null) {
            getSession().delete(program);
        }
    }

    @Override
    public Program findById(Long id) {
        return getSession().get(Program.class, id);
    }

    @Override
    public List<Program> findAll() {
        Query<Program> query = getSession().createQuery("FROM Program", Program.class);
        return query.getResultList();
    }

    @Override
    public List<Program> findActivePrograms() {
        Query<Program> query = getSession()
            .createQuery("FROM Program WHERE isActive = true", Program.class);
        return query.getResultList();
    }

    @Override
    public List<Program> findByCategory(Long categoryId) {
        Query<Program> query = getSession()
            .createQuery("FROM Program WHERE category.id = :categoryId", Program.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }

    @Override
    public List<Program> searchByName(String name) {
        Query<Program> query = getSession()
            .createQuery("FROM Program WHERE LOWER(name) LIKE :name", Program.class);
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }
}