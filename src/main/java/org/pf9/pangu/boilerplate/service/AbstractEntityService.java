package org.pf9.pangu.boilerplate.service;

import org.pf9.pangu.boilerplate.util.EntityUtils;
import org.pf9.pangu.boilerplate.util.MapperBase;
import org.pf9.pangu.framework.data.domain.AbstractAuditingEntity;
import org.pf9.pangu.framework.data.domain.SoftDeleteable;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.core.ResolvableType;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class AbstractEntityService<T extends Serializable, M extends MapperBase> implements EntityService<T, M> {

    // FIXME: 最好能在这里直接注入，不用再在子类里注入
    protected M baseMapper;

    protected final Class<T> entityClass;

    public AbstractEntityService() {

        ResolvableType resolvableType = ResolvableType.forClass(getClass());
        entityClass = (Class<T>) resolvableType.as(AbstractEntityService.class).getGeneric(0).resolve();
    }

    public void setBaseMapper(M baseMapper) {
        this.baseMapper = baseMapper;
    }


    @Override
    public T newEntity() {
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public int insert(T entity) {

        if (entity instanceof AbstractAuditingEntity) {
            EntityUtils.setCreatedAudit((AbstractAuditingEntity) entity);
        }
        return baseMapper.insert(entity);
    }

    @Override
    public int insertSelective(T entity) {
        if (entity instanceof AbstractAuditingEntity) {
            EntityUtils.setCreatedAudit((AbstractAuditingEntity) entity);
        }
        return baseMapper.insertSelective(entity);
    }

    @Override
    public int updateByPrimaryKeySelective(T entity) {
        if (entity instanceof AbstractAuditingEntity) {
            EntityUtils.setModifiedAudit((AbstractAuditingEntity) entity);
        }
        return baseMapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findOne(T entity) {
        return Optional.ofNullable((T) baseMapper.selectOne(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        try {
            Field deletedField = entityClass.getField("deleted");
            Condition condition = new Condition(entityClass);
            condition.createCriteria().andEqualTo("deleted", false);
            return baseMapper.selectByCondition(condition);
        } catch (NoSuchFieldException ex) {
            return baseMapper.selectAll();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(Long id) {
        return Optional.ofNullable((T) baseMapper.selectByPrimaryKey(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(String id) {
        return Optional.ofNullable((T) baseMapper.selectByPrimaryKey(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findByCode(String code) {
        Condition condition = new Condition(entityClass);
        condition.createCriteria().andEqualTo("code", code);
        return baseMapper.selectByCondition(condition);
    }


    @Override
    public List<T> findByCondition(Object condition) {
        return baseMapper.selectByCondition(condition);
    }

    @Override
    @Transactional
    public int update(T entity) {
        if (entity instanceof AbstractAuditingEntity) {
            EntityUtils.setModifiedAudit((AbstractAuditingEntity) entity);
        }
        return baseMapper.updateByPrimaryKey(entity);
    }

    @Override
    @Transactional
    public void updateById(String key, Object value, Long id) {
        throw new NotImplementedException("该类未实现");
    }

    @Override
    @Transactional
    public void updateById(String key, Object value, T entity) {
        throw new NotImplementedException("该类未实现");
    }

    @Override
    @Transactional
    public int delete(T entity) {

        if (entity instanceof AbstractAuditingEntity) {
            EntityUtils.setModifiedAudit((AbstractAuditingEntity) entity);
        }
        return baseMapper.delete(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        baseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int softDelete(T entity) {
        if (entity instanceof SoftDeleteable) {
            ((SoftDeleteable) entity).markDeleted();
        }

        return baseMapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public int softDeleteById(Long id) {
        // FIXME: 实现
        throw new NotImplementedException("该类未实现");
    }
}
