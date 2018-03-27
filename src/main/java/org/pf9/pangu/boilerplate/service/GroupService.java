package org.pf9.pangu.boilerplate.service;

import org.pf9.pangu.boilerplate.entity.*;
import org.pf9.pangu.boilerplate.mapper.GroupMapper;
import org.pf9.pangu.boilerplate.service.dto.GroupDto;

import java.util.List;

public interface GroupService extends EntityService<Group,GroupMapper> {

    Group saveOrUpdateGroup(Group group);

    GroupDto getTree();

    List<DictionaryItem> getProductByGroup(String groupCode);

    List<User> getUserByGroup(String groupCode);

    int assignUserToGroup(List<UserGroupAssoc> assocs);

    int assignProductToGroup(List<GroupDictionaryItemAssoc> assocs);

    int setDsiabledOrNot(String code);

    int setDeletedOrNot(String code);

    List<DictionaryItem> getAllProduct();

}