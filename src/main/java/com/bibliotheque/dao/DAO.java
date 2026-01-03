package com.bibliotheque.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> findById(int id);
    List<T> findAll();
    void save(T t);
    void update(T t);
    void delete(int id);
}