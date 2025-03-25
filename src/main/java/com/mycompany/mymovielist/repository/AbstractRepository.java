/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;

import java.util.*;

/**
 *
 * @author kiran
 */
public abstract class AbstractRepository<T, ID> implements BaseRepository<T, ID> {
    protected final Map<ID, T> items = new HashMap<>();

    @Override
    public void add(T item) {
        items.put((ID) getId(item), item);
    }

    @Override
    public void update(T item) {
        items.put((ID) getId(item), item);
    }

    @Override
    public void remove(ID id) {
        items.remove(id);
    }

    @Override
    public Optional<T> get(ID id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(items.values());
    }

    protected abstract ID getId(T item); 
}

