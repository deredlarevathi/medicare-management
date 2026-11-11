package com.medicare.dao;

import java.sql.SQLException;
import java.util.List;

/** Interface + generics: every DAO promises the same basic operations. */
public interface CrudDAO<T> {
    List<T> findAll() throws SQLException;
    void add(T item) throws SQLException;
    void delete(int id) throws SQLException;
}
