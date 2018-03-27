package org.pf9.pangu.boilerplate.service.impl;

import org.pf9.pangu.boilerplate.entity.*;
import org.pf9.pangu.boilerplate.mapper.DictionaryItemMapper;
import org.pf9.pangu.boilerplate.mapper.GroupDictionaryItemAssocMapper;
import org.pf9.pangu.boilerplate.mapper.GroupMapper;
import org.pf9.pangu.boilerplate.mapper.UserGroupAssocMapper;
import org.pf9.pangu.boilerplate.seq.GroupSeq;
import org.pf9.pangu.boilerplate.service.AbstractEntityService;
import org.pf9.pangu.boilerplate.service.GroupService;
import org.pf9.pangu.boilerplate.service.dto.GroupDto;
import org.pf9.pangu.boilerplate.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl extends AbstractEntityService<Group, GroupMapper> implements GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private UserGroupAssocMapper userGroupAssocMapper;

    @Autowired
    private DictionaryItemMapper dictionaryItemMapper;

    @Autowired
    private GroupDictionaryItemAssocMapper groupDictionaryItemAssocMapper;


//    @Autowired
//    @Qualifier("pgSequenceGenerator")
//    private SequenceGenerator sequenceGenerator;

    @Autowired
    private GroupSeq groupSeq;

    private Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);


    @PostConstruct
    protected void init() {
        logger.debug("Group BaseMapper is " + groupMapper.toString());
        super.setBaseMapper(groupMapper);
    }


    @Transactional
    @Override
    public Group saveOrUpdateGroup(Group group) {
        if(group.getId()!=null){
            if(group.getParentId()==null){
                group.setParentId(0l);
            }
            EntityUtils.setModifiedAudit(group);
            groupMapper.updateByPrimaryKeySelective(group);
        }else{
            group.setId(groupSeq.nextValue());
            EntityUtils.setCreatedAudit(group);
            group.setDeleted(false);
            group.setDisabled(false);
            if (group.getParentId() == null) {
                group.setParentId(0l);
            }
            groupMapper.insert(group);
        }
        return group;
    }

    @Override
    public List<DictionaryItem> getProductByGroup(String groupCode) {
        return groupDictionaryItemAssocMapper.getProductByGroup(groupCode);
    }



    @Override
    public List<User> getUserByGroup(String groupCode) {
        return userGroupAssocMapper.getUserByGroup(groupCode);
    }

    @Override
    @Transactional
    public int assignUserToGroup(List<UserGroupAssoc> assocs) {
        try {
            Condition condition = new Condition(UserGroupAssoc.class);
            condition.createCriteria().andEqualTo("groupCode", assocs.get(0).getGroupCode());
            userGroupAssocMapper.deleteByCondition(condition);
            for (UserGroupAssoc assoc : assocs) {
                assoc.setId(groupSeq.nextValue());
                userGroupAssocMapper.insert(assoc);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    @Transactional
    public int assignProductToGroup(List<GroupDictionaryItemAssoc> assocs) {
        try {
            Condition condition = new Condition(GroupDictionaryItemAssoc.class);
            condition.createCriteria().andEqualTo("groupCode", assocs.get(0).getGroupCode());
            groupDictionaryItemAssocMapper.deleteByCondition(condition);
            for (GroupDictionaryItemAssoc assoc : assocs) {
                assoc.setId(groupSeq.nextValue());
                groupDictionaryItemAssocMapper.insert(assoc);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    @Override
    public List<DictionaryItem> getAllProduct(){
        Condition condition = new Condition(DictionaryItem.class);
        condition.createCriteria().andEqualTo("dicId",1);
        return dictionaryItemMapper.selectByCondition(condition);
    }

    @Transactional
    @Override
    public int setDsiabledOrNot(String code){
        Condition condition = new Condition(Group.class);
        condition.createCriteria().andEqualTo("code",code);
        Group group = groupMapper.selectByCondition(condition).get(0);
        if(group.isDisabled()){
            group.setDisabled(false);
        }else{
            group.setDisabled(true);
        }
        return groupMapper.updateByPrimaryKeySelective(group);
    }

    @Transactional
    @Override
    public int setDeletedOrNot(String code){
        Condition condition = new Condition(Group.class);
        condition.createCriteria().andEqualTo("code",code);
        Group group = groupMapper.selectByCondition(condition).get(0);
        if(group.isDeleted()){
            group.setDeleted(false);
        }else{
            group.setDeleted(true);
        }
        return groupMapper.updateByPrimaryKeySelective(group);
    }

    public Group getGroupById(long id) {
        return groupMapper.selectByPrimaryKey(id);
    }

    public GroupDto getTree() {
        List<Group> groupList = getAllGroup();
        Group rootGroup = groupMapper.selectByPrimaryKey(1l);
        GroupDto groupTree = new GroupDto(rootGroup);
        if (groupTree != null) {
            List<Group> children = getChildren(groupList, 1l);
            for (Group group : children) {
                GroupDto dto = new GroupDto(group);
                dto.setParentGroup(getGroupById(group.getParentId()));
                groupTree.addChild(dto);
            }
            getGroupTree(groupList, groupTree.getChildren());
        }
        return groupTree;
    }


    List<Group> getAllGroup() {
        Condition condition = new Condition(Group.class);
        condition.createCriteria().andEqualTo("isDisabled",false)
                .andEqualTo("isDeleted",false);

        List<Group> groups = groupMapper.selectByCondition(condition);

        return groups.stream().sorted(new Comparator<Group>() {
            @Override
            public int compare(Group o1, Group o2) {
                if (o1.getSort() == null)
                    o1.setSort(9999l);
                if (o2.getSort() == null)
                    o2.setSort(9999l);
                return (int) (o1.getSort() - o2.getSort());
            }
        }).collect(Collectors.toList());
    }

    public List<Group> getChildren(List<Group> all, Long id) {
        return all.stream().filter(g -> g.getParentId().equals(id)).collect(Collectors.toList());
    }

    public void getGroupTree(List<Group> all, List<GroupDto> nodes) {
        for (GroupDto node : nodes) {
            List<Group> children = getChildren(all, node.getId());
            for (Group group : children) {
                GroupDto dto = new GroupDto(group);
                dto.setParentGroup(getGroupById(group.getParentId()));
                node.addChild(dto);
            }
            if (node.getChildren() != null && node.getChildren().size() > 0) {
                getGroupTree(all, node.getChildren());
            }
        }
    }

}
