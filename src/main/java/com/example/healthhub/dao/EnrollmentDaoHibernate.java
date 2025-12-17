package com.example.healthhub.dao;

import com.example.healthhub.model.Enrollment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class EnrollmentDaoHibernate implements EnrollmentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Enrollment enrollment) {
        getSession().save(enrollment);
    }

    @Override
    public void update(Enrollment enrollment) {
        getSession().update(enrollment);
    }

    @Override
    public void delete(Long id) {
        Enrollment enrollment = findById(id);
        if (enrollment != null) {
            getSession().delete(enrollment);
        }
    }

    @Override
    public Enrollment findById(Long id) {
        return getSession().get(Enrollment.class, id);
    }

    @Override
    public List<Enrollment> findAll() {
        Query<Enrollment> query = getSession().createQuery("FROM Enrollment", Enrollment.class);
        return query.getResultList();
    }


    @Override
    public List<Enrollment> findByProgram(Long programId) {
        Query<Enrollment> query = getSession()
            .createQuery("FROM Enrollment e WHERE e.program.id = :programId", Enrollment.class);
        query.setParameter("programId", programId);
        return query.getResultList();
    }

    @Override
    public List<Enrollment> findActiveEnrollments() {
        Query<Enrollment> query = getSession()
            .createQuery("FROM Enrollment e WHERE e.status = 'Active'", Enrollment.class);
        return query.getResultList();
    }

     @Override
    public Long countAllEnrollments() {
        Query<Long> query = getSession().createQuery("SELECT COUNT(e.id) FROM Enrollment e", Long.class);
        return query.uniqueResult();
    }

    @Override
    public List<Object[]> findTopEnrolledPrograms(int limit) {
        String hql = "SELECT e.program, COUNT(e.id) AS enrollmentCount " +
                     "FROM Enrollment e " +
                     "GROUP BY e.program " +
                     "ORDER BY enrollmentCount DESC";

        Query<Object[]> query = getSession().createQuery(hql, Object[].class);
        query.setMaxResults(limit); 
        return query.getResultList();
    }

    @Override
    public List<Enrollment> findByMember(Long memberId) {
        Query<Enrollment> query = getSession()
            .createQuery("FROM Enrollment e WHERE e.member.id = :memberId", Enrollment.class);
        query.setParameter("memberId", memberId);
        return query.getResultList();
    }
}