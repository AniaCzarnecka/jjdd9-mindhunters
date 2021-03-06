package com.infoshareacademy.repository;

import com.infoshareacademy.domain.Role;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Optional;

@Stateless
public class RoleRepositoryBean {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Role> findByRoleName(String role) {
        Query query = entityManager.createNamedQuery("Role.findByRole");
        query.setParameter("role", role);
        return query.getResultList().stream().findFirst();
    }
}
