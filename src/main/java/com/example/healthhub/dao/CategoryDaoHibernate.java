package com.example.healthhub.dao;

import com.example.healthhub.model.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class CategoryDaoHibernate implements CategoryDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Category category) {
        getSession().save(category);
    }

    @Override
    public void update(Category category) {
        getSession().update(category);
    }

    @Override
    public void delete(Long id) {
        Category category = findById(id);
        if (category != null) {
            getSession().delete(category);
        }
    }

    @Override
    public Category findById(Long id) {
        return getSession().get(Category.class, id);
    }

    @Override
    public List<Category> findAll() {
        Query<Category> query = getSession().createQuery("FROM Category", Category.class);
        return query.getResultList();
    }

    @Override
    public Category findByName(String name) {
        Query<Category> query = getSession()
            .createQuery("FROM Category WHERE name = :name", Category.class);
        query.setParameter("name", name);
        return query.uniqueResult();
    }
}