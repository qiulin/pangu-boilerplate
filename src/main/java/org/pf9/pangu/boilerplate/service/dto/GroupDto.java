package org.pf9.pangu.boilerplate.service.dto;

import org.pf9.pangu.boilerplate.entity.Group;
import org.pf9.pangu.boilerplate.entity.User;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GroupDto implements Serializable{

    private static final long serialVersionUID = -7209083677967362035L;

    private Long id;

    private Group parentGroup;

    private Long sort;

    private String code;

    private String name;

    private String description;

    private User createUser;

    private User modifiedUser;

    private Date createDate;

    private Date modifiedDate;

    private List<GroupDto> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(Group parentGroup) {
        this.parentGroup = parentGroup;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getModifiedUser() {
        return modifiedUser;
    }

    public void setModifiedUser(User modifiedUser) {
        this.modifiedUser = modifiedUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public List<GroupDto> getChildren() {
        return children;
    }

    public void setChildren(List<GroupDto> children) {
        this.children = children;
    }


    public GroupDto() {

    }
    public void addChild(GroupDto groupDto){
        if (this.children == null) {
            this.children = new LinkedList<>();
        }
        this.children.add(groupDto);
    }
    public GroupDto(Group group) {
        this.id = group.getId();
        this.sort = group.getSort();
        this.code = group.getCode();
        this.name = group.getName();
        this.description = group.getDescription();
        this.createDate = Date.from(group.getCreatedDate());
        this.modifiedDate = Date.from(group.getLastModifiedDate());
    }
}
