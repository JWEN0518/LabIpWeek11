package com.example.healthhub.dao;

import com.example.healthhub.model.BmiRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class BmiRecordDAOHibernate implements BmiRecordDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(BmiRecord record) {
        getSession().save(record);
    }

    @Override
    public void update(BmiRecord record) {
        getSession().update(record);
    }

    @Override
    public void delete(Long id) {
        BmiRecord record = findById(id);
        if (record != null) {
            getSession().delete(record);
        }
    }

    @Override
    public BmiRecord findById(Long id) {
        return getSession().get(BmiRecord.class, id);
    }

    @Override
    public List<BmiRecord> findAll() {
        Query<BmiRecord> query = getSession().createQuery("FROM BmiRecord", BmiRecord.class);
        return query.getResultList();
    }

    @Override
    public List<BmiRecord> findByMember(Long memberId) {
        // ✅ ALIAS ADDED: 'b'
        Query<BmiRecord> query = getSession()
            .createQuery("FROM BmiRecord b WHERE b.member.id = :memberId ORDER BY b.recordedAt DESC", BmiRecord.class);
        query.setParameter("memberId", memberId);
        return query.getResultList();
    }

    @Override
    public List<BmiRecord> findRecent(int limit) {
        // ✅ ALIAS ADDED: 'b'
        Query<BmiRecord> query = getSession()
            .createQuery("FROM BmiRecord b ORDER BY b.recordedAt DESC", BmiRecord.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}