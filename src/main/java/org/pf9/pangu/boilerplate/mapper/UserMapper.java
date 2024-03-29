package org.pf9.pangu.boilerplate.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.util.MapperBase;

import java.util.List;

@Mapper
public interface UserMapper extends MapperBase<User> {

    User findUserByLogin(@Param("login") String login);

    List<User> findAdminUsers(String roleCode);

    int deleteUserById(long id);

}
