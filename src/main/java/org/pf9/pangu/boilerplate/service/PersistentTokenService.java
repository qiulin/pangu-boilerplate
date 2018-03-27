package org.pf9.pangu.boilerplate.service;

import org.pf9.pangu.boilerplate.entity.PersistentToken;
import org.pf9.pangu.boilerplate.mapper.PersistentTokenMapper;

import java.util.Optional;

public interface PersistentTokenService extends EntityService<PersistentToken, PersistentTokenMapper> {

    void saveOrUpdate(PersistentToken token);

    Optional<PersistentToken> findOneBySeries(String series);
}
