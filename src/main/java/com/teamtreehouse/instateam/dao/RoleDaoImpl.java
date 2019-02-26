package com.teamtreehouse.instateam.dao;

import com.teamtreehouse.instateam.model.Role;
import com.teamtreehouse.instateam.service.ProjectService;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ProjectService projectService;

    @Override
    public List<Role> findAll() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Role> query = builder.createQuery(Role.class);
        query.from(Role.class);
        List<Role> roles = session.createQuery(query).getResultList();
        session.close();
        return roles;
    }

    @Override
    public Role findById(Long id) {
        Session session = sessionFactory.openSession();
        Role role = session.get(Role.class,id);
        Hibernate.initialize(role.getCollaborators());
        session.close();
        return role;
    }

    @Override
    public void save(Role role) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(role);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(Role role) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(role);
        session.getTransaction().commit();
        session.close();
    }
}
