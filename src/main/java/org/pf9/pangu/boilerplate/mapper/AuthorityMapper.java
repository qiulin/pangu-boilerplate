package org.pf9.pangu.boilerplate.mapper;

import org.pf9.pangu.boilerplate.entity.Authority;
import org.pf9.pangu.boilerplate.util.MapperBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthorityMapper extends MapperBase<Authority> {

    List<Authority> findAuthoritiesByUserId(@Param("uid") Long uid);

    long addAuthority(Authority authority);

}
