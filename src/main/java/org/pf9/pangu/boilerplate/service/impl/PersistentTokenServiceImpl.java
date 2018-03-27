package org.pf9.pangu.boilerplate.service.impl;

import org.pf9.pangu.boilerplate.entity.PersistentToken;
import org.pf9.pangu.boilerplate.mapper.PersistentTokenMapper;
import org.pf9.pangu.boilerplate.service.AbstractEntityService;
import org.pf9.pangu.boilerplate.service.PersistentTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class PersistentTokenServiceImpl extends AbstractEntityService<PersistentToken, PersistentTokenMapper> implements PersistentTokenService {

    @Autowired
    private PersistentTokenMapper baseMapper;

    @PostConstruct
    private void init() {
        super.setBaseMapper(baseMapper);
    }

    @Override
    public void saveOrUpdate(PersistentToken token) {

        this.baseMapper.insert(token);
    }

    @Override
    public Optional<PersistentToken> findOneBySeries(String series) {
        Condition condition = new Condition(PersistentToken.class);
        condition.createCriteria().andCondition("series", series);
        return this.baseMapper.selectByCondition(condition).stream().findFirst();
    }
}
