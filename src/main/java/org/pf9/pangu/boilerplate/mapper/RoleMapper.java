package org.pf9.pangu.boilerplate.mapper;

import org.pf9.pangu.boilerplate.entity.Role;
import org.pf9.pangu.boilerplate.util.MapperBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends MapperBase<Role> {

    List<Role> getRoleListByUser(@Param("userId") Long userId);
}
