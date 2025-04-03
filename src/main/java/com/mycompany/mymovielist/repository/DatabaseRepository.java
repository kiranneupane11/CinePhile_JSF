/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;
import com.mycompany.mymovielist.util.EMFProvider;

/**
 *
 * @author kiran
 */
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public abstract class DatabaseRepository<T, ID> extends AbstractRepository<T, ID> {
    protected EntityManager entityManager;
    private final Class<T> entityType;

    protected DatabaseRepository(Class<T> entityType, EntityManager entityManager) {
        this.entityType = entityType;
        this.entityManager = EMFProvider.getEntityManager();
    }

    @Override
    public void add(T item) {
        entityManager.getTransaction().begin();
        entityManager.persist(item);
        entityManager.getTransaction().commit();
    }

    @Override
    public void update(T item) {
        entityManager.getTransaction().begin();
        entityManager.merge(item);
        entityManager.getTransaction().commit();
    }

    @Override
    public void remove(T item) {
        entityManager.getTransaction().begin();
        get(getId(item)).ifPresent(entity -> entityManager.remove(entity));
        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<T> get(ID id) {
        T entity = entityManager.find(entityType, id);
            if (entity != null) {
                entityManager.refresh(entity);  
            }
            return Optional.ofNullable(entity);    
    }
    @Override
    public List<T> getAll() {
        List<T> items = entityManager.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType)
                .getResultList();
        for (T item : items) {
            entityManager.refresh(item);
        }
        System.out.println("Items fetched: " + items.size());
        return items;
    }
    
    @Override
    protected ID getId(T item) {
        throw new UnsupportedOperationException("getId() must be implemented in subclasses.");
    }
}

