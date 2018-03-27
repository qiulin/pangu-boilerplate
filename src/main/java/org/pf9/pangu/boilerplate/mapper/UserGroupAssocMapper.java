package org.pf9.pangu.boilerplate.mapper;

import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.entity.UserGroupAssoc;
import org.pf9.pangu.boilerplate.util.MapperBase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserGroupAssocMapper extends MapperBase<UserGroupAssoc>{

    List<User> getUserByGroup(String groupCode);
}
