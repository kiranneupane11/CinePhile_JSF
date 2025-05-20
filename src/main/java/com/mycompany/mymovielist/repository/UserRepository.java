/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;

import com.mycompany.mymovielist.model.*;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
/**
 *
 * @author kiran
 */
@Named
@ApplicationScoped
public class UserRepository extends DatabaseRepository<User, Long> {
    @Inject
    public UserRepository() {
        super(User.class);
    }
    
    public Optional<User> findByUsername(String username) {
        return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }
    
    public Optional<User> findByEmail(String email) {
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst();
    }
    
   public List<User> findPagedSortedFiltered(int offset,
                                          int pageSize,
                                          String sortField,
                                          boolean asc,
                                          Map<String, String> filters) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<User> cq = cb.createQuery(User.class);
    Root<User> root = cq.from(User.class);

    // 1) Build predicates from filters
    List<Predicate> preds = new ArrayList<>();
    filters.forEach((field, value) -> {
        if (value == null || value.isEmpty()) return;

        Path<?> path = root.get(field);
        Class<?> type = path.getJavaType();

        if (Number.class.isAssignableFrom(type)) {
            try {
                if (type.equals(Integer.class)) {
                    Integer number = Integer.valueOf(value);
                    preds.add(cb.equal(path, number));
                } else if (type.equals(Long.class)) {
                    Long number = Long.valueOf(value);
                    preds.add(cb.equal(path, number));
                } else if (type.equals(Double.class)) {
                    Double number = Double.valueOf(value);
                    preds.add(cb.equal(path, number));
                } else {
                    throw new NumberFormatException("Unsupported numeric type: " + type.getSimpleName());
                }
            } catch (NumberFormatException e) {
            }
        } else if (type.equals(String.class)) {
            preds.add(cb.like(cb.lower(path.as(String.class)),
                              value.toLowerCase() + "%"));
        } else if (Enum.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum> enumType = (Class<? extends Enum>) type;
            try {
                String enumName = value.toUpperCase();
                Enum enumVal = Enum.valueOf(enumType, enumName);
                preds.add(cb.equal(path, enumVal));
            } catch (IllegalArgumentException ex) {
            }
        } else {
        }
    });
    if (!preds.isEmpty()) {
        cq.where(cb.and(preds.toArray(new Predicate[0])));
    }

    // 2) Sorting
    if (sortField != null) {
        Path<?> sortPath = root.get(sortField);
        Order order = asc ? cb.asc(sortPath) : cb.desc(sortPath);
        cq.orderBy(order);
    }

    TypedQuery<User> q = em.createQuery(cq);
    q.setFirstResult(offset);
    q.setMaxResults(pageSize);
    return q.getResultList();
}
    
    public long countFiltered(Map<String,String> filters) {
        CriteriaBuilder cb    = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root       = cq.from(User.class);

        // apply filter logic
        List<Predicate> preds = new ArrayList<>();
        filters.forEach((field, value) -> {
            if (value == null || value.isEmpty()) return;
            Path<String> path = root.get(field);
            if ("role".equals(field)) {
                preds.add(cb.equal(path, value));
            } else {
                preds.add(cb.like(cb.lower(path), value.toLowerCase() + "%"));
            }
        });
        cq.select(cb.count(root));
        if (!preds.isEmpty()) {
            cq.where(cb.and(preds.toArray(new Predicate[0])));
        }
        return em.createQuery(cq).getSingleResult();
    }
}
   
