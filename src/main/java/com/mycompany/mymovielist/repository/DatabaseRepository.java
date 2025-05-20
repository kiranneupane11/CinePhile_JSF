/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;

/**
 *
 * @author kiran
 */
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public abstract class DatabaseRepository<T, ID> extends AbstractRepository<T, ID> {
    
    @PersistenceContext(unitName="MovieListPU")
    protected EntityManager em; 
    
    private final Class<T> entityType;

    public DatabaseRepository(Class<T> entityType) {
        this.entityType = entityType;
    }

    @Override
    @Transactional
    public void add(T item) {
        em.persist(item);
    }

    @Override
    @Transactional
    public void update(T item) {
        em.merge(item);
    }

    @Override
    @Transactional
    public void remove(T item) {
        em.remove(em.contains(item) ? item : em.merge(item));
    }

    @Override
    public Optional<T> get(ID id) {
        T entity = em.find(entityType, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<T> getAll() {
        return em.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType)
                 .getResultList();
    }
    
    public List<T> findAllPaged(int offset, int pageSize) {
        return em.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType)
                 .setFirstResult(offset)
                 .setMaxResults(pageSize)
                 .getResultList();
    }

    public long countAll() {
        return em.createQuery("SELECT COUNT(e) FROM " + entityType.getSimpleName() + " e", Long.class)
                 .getSingleResult();
    }

    @Override
    protected ID getId(T item) {
        throw new UnsupportedOperationException("Subclasses must implement getId().");
    }
}
