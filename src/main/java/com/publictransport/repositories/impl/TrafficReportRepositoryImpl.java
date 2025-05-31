package com.publictransport.repositories.impl;

import com.publictransport.models.ApprovalStatus;
import com.publictransport.models.TrafficReport;
import com.publictransport.repositories.TrafficReportRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public class TrafficReportRepositoryImpl implements TrafficReportRepository {
    @Autowired
    private LocalSessionFactoryBean factory;


    @Override
    public TrafficReport save(TrafficReport report) {
        Session session = factory.getObject().getCurrentSession();
        if (report.getId() == null) {
            session.persist(report);
        } else {
            session.merge(report);
        }
        return report;
    }

    @Override
    public TrafficReport findById(Long id) {
        Session session = factory.getObject().getCurrentSession();
        return session.get(TrafficReport.class, id);
    }

    @Override
    public List<TrafficReport> findByApprovalStatus(ApprovalStatus approvalStatus) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TrafficReport> query = builder.createQuery(TrafficReport.class);
        Root<TrafficReport> root = query.from(TrafficReport.class);

        query.select(root);
        query.where(builder.equal(root.get("approvalStatus"), approvalStatus));

        return session.createQuery(query).getResultList();
    }

    @Override
    public List<TrafficReport> findByLocationContaining(String location) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TrafficReport> query = builder.createQuery(TrafficReport.class);
        Root<TrafficReport> root = query.from(TrafficReport.class);

        query.select(root);
        query.where(builder.like(builder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));

        return session.createQuery(query).getResultList();
    }

    @Override
    public void delete(TrafficReport report) {
        Session session = factory.getObject().getCurrentSession();
        session.delete(report);
    }
}
