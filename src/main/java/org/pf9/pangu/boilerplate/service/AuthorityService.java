package org.pf9.pangu.boilerplate.service;


import org.pf9.pangu.boilerplate.entity.Authority;
import org.pf9.pangu.boilerplate.mapper.AuthorityMapper;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.service.CreateDefaultService;

import java.util.List;

public interface AuthorityService extends EntityService<Authority, AuthorityMapper>, CreateDefaultService {

    List<Authority> findAuthoritiesByUserId(Long uid);

    List<Authority> findAll();

    long addAuthority(Authority authority);

    int deleteAuthorityById(String code);

    DataTablesResult<Authority> getTable(DataTablesQuery query);
}
