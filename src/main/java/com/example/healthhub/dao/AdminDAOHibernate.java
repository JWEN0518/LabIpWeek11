package com.example.healthhub.dao;

import com.example.healthhub.model.Admin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AdminDAOHibernate implements AdminDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Admin admin) {
        getSession().save(admin);
    }

    @Override
    public void update(Admin admin) {
        getSession().update(admin);
    }

    @Override
    public void delete(Long id) {
        Admin admin = findById(id);
        if (admin != null) {
            getSession().delete(admin);
        }
    }

    @Override
    public Admin findById(Long id) {
        return getSession().get(Admin.class, id);
    }

    @Override
    public List<Admin> findAll() {
        Query<Admin> query = getSession().createQuery("FROM Admin", Admin.class);
        return query.getResultList();
    }

    @Override
    public Admin findByUsername(String username) {
        Query<Admin> query = getSession()
            .createQuery("FROM Admin WHERE username = :username", Admin.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    @Override
    public Admin authenticate(String username, String password) {
        Query<Admin> query = getSession()
            .createQuery("FROM Admin WHERE username = :username AND password = :password", 
                        Admin.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.uniqueResult();
    }
}