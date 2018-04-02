package org.pf9.pangu.boilerplate.service;

import org.pf9.pangu.boilerplate.entity.Group;
import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.entity.UserGroupAssoc;
import org.pf9.pangu.boilerplate.mapper.GroupMapper;
import org.pf9.pangu.boilerplate.service.dto.GroupDto;

import java.util.List;

public interface GroupService extends EntityService<Group, GroupMapper> {

    Group saveOrUpdateGroup(Group group);

    GroupDto getTree();

    List<User> getUserByGroup(String groupCode);

    int assignUserToGroup(List<UserGroupAssoc> assocs);
}