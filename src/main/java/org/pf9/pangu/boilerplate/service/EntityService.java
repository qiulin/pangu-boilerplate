package org.pf9.pangu.boilerplate.service;


import org.pf9.pangu.boilerplate.util.MapperBase;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface EntityService<T extends Serializable, M extends MapperBase> {

    // new
    T newEntity();

    // insert
    int insert(T entity);

    int insertSelective(T entity);

    // select
    Optional<T> findOne(T entity);

    List<T> findAll();

    Optional<T> findById(Long id);

    Optional<T> findById(String id);

    List<T> findByCode(String code);

    List<T> findByCondition(Object condition);

    // Update
    int update(T entity);

    void updateById(String key, Object value, Long id);

    void updateById(String key, Object value, T entity);

    int updateByPrimaryKeySelective(T entity);

    // Delete
    int delete(T entity);

    void deleteById(Long id);

    int softDelete(T entity);

    int softDeleteById(Long id);
}
