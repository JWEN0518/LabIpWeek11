package com.example.healthhub.dao;

import com.example.healthhub.model.Member;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class MemberDaoHibernate implements MemberDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Member member) {
        getSession().save(member);
    }

    @Override
    public void update(Member member) {
        getSession().update(member);
    }

    @Override
    public void delete(Long id) {
        Member member = findById(id);
        if (member != null) {
            getSession().delete(member);
        }
    }

    @Override
public Member findById(Long id) {
    try {
        Member member = getSession().get(Member.class, id);
        
        if (member != null) {
            org.hibernate.Hibernate.initialize(member.getBmiHistory());
            
            if (member.getBmiHistory() != null && !member.getBmiHistory().isEmpty()) {
                member.getBmiHistory().sort((b1, b2) -> 
                    b2.getRecordedAt().compareTo(b1.getRecordedAt())
                );
            }
        }
        
        return member;
    } catch (Exception e) {
        System.err.println("Error loading member: " + e.getMessage());
        throw e;
    }
}

    @Override
    public List<Member> findAll() {
        Query<Member> query = getSession().createQuery("FROM Member", Member.class);
        return query.getResultList();
    }

    @Override
    public Member findByUsername(String username) {
        Query<Member> query = getSession()
            .createQuery("FROM Member m WHERE m.username = :username", Member.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    @Override
    public Member authenticate(String username, String password) {
        Query<Member> query = getSession()
            .createQuery("FROM Member m WHERE m.username = :username AND m.password = :password", Member.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.uniqueResult();
    }

    @Override
    public List<Member> findByMemberType(String memberType) {
        Query<Member> query = getSession()
            .createQuery("FROM Member m WHERE m.memberType = :type", Member.class);
        query.setParameter("type", memberType);
        return query.getResultList();
    }

    @Override
    public List<Member> searchByName(String name) {
        Query<Member> query = getSession()
            .createQuery("FROM Member m WHERE LOWER(m.name) LIKE :name", Member.class);
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }
}