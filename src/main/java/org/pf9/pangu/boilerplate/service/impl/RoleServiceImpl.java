package org.pf9.pangu.boilerplate.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.pf9.pangu.boilerplate.entity.Authority;
import org.pf9.pangu.boilerplate.entity.Menu;
import org.pf9.pangu.boilerplate.entity.Role;
import org.pf9.pangu.boilerplate.entity.RoleAuthorityAssoc;
import org.pf9.pangu.boilerplate.entity.RoleMenuAssoc;
import org.pf9.pangu.boilerplate.entity.UserRoleAssoc;
import org.pf9.pangu.boilerplate.mapper.AuthorityMapper;
import org.pf9.pangu.boilerplate.mapper.RoleAuthorityAssocMapper;
import org.pf9.pangu.boilerplate.mapper.RoleMapper;
import org.pf9.pangu.boilerplate.mapper.RoleMenuAssocMapper;
import org.pf9.pangu.boilerplate.mapper.UserRoleAssocMapper;
import org.pf9.pangu.boilerplate.seq.RoleAuthorityAssocSeq;
import org.pf9.pangu.boilerplate.seq.RoleMenuAssocSeq;
import org.pf9.pangu.boilerplate.seq.RoleSeq;
import org.pf9.pangu.boilerplate.service.AbstractEntityService;
import org.pf9.pangu.boilerplate.service.RoleService;
import org.pf9.pangu.boilerplate.util.DataTablesUtil;
import org.pf9.pangu.boilerplate.util.EntityUtils;
import org.pf9.pangu.framework.common.exception.PanguException;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesOrder;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesSearch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class RoleServiceImpl extends AbstractEntityService<Role, RoleMapper> implements RoleService {

    @Autowired
    private RoleMapper baseMapper;

    @Autowired
    private RoleMenuAssocMapper roleMenuAssocMapper;

    @Autowired
    private RoleAuthorityAssocMapper roleAuthorityAssocMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private RoleSeq roleSeq;

    @Autowired
    private RoleMenuAssocSeq roleMenuAssocSeq;

    @Autowired
    private RoleAuthorityAssocSeq roleAuthorityAssocSeq;

    @Autowired
    private UserRoleAssocMapper userRoleAssocMapper;

    @PostConstruct
    private void init() {
        super.setBaseMapper(baseMapper);
    }

    @Override
    public DataTablesResult<Role> getTable(DataTablesQuery query) {

        int pageIndex = query.getStart() / query.getLength() + 1;
        int pageSize = query.getLength();

        List<DataTablesOrder> orders = query.getOrder();

        DataTablesSearch search = query.getSearch();
        Condition condition = new Condition(Role.class);

        String searchText = search.getValue();

        if (!StringUtils.isEmpty(searchText))
            condition.createCriteria().andLike("name", "%" + searchText + "%")
                    .orLike("code", "%" + searchText + "%")
                    .orLike("description", "%" + searchText + "%");

        for (DataTablesOrder order : orders) {
            condition.setOrderByClause(query.getColumns().get(order.getColumn()).getData() + " " + order.getDir());
        }

        Page<Role> rolesPaged = PageHelper.startPage(pageIndex, pageSize)
                .doSelectPage(() -> baseMapper.selectByCondition(condition));

        return DataTablesUtil.wrap(rolesPaged, query);

    }

    /**
     * 把角色和菜单建立关系
     *
     * @param role
     * @param menu
     */
    @Override
    @Transactional
    public void assignMenuToRole(Role role, Menu menu) {
        Condition condition = new Condition(RoleMenuAssoc.class);
        condition.createCriteria().andEqualTo("role_code", role.getCode())
                .andEqualTo("menu_id", menu.getId());

        List<RoleMenuAssoc> assocs = roleMenuAssocMapper.selectByCondition(condition);

        if (assocs.size() == 0) {
            RoleMenuAssoc assoc = new RoleMenuAssoc();
            assoc.setId(roleMenuAssocSeq.nextValue());
            assoc.setMenuId(menu.getId());
            assoc.setRoleCode(role.getCode());
            roleMenuAssocMapper.insert(assoc);
        }
    }

    @Override
    @Transactional
    public void assignMenuToRole(List<RoleMenuAssoc> assocList) {

        // 先清除所有的关联，然后重新建立关联，假设参数里面的关联都是同一个角色的
        String roleCode = assocList.get(0).getRoleCode();

        Condition assocCondition = new Condition(RoleMenuAssoc.class);
        assocCondition.createCriteria().andEqualTo("roleCode", roleCode);
        roleMenuAssocMapper.deleteByCondition(assocCondition);
        if (assocList.get(0).getMenuId() != null) {
            for (RoleMenuAssoc ac : assocList) {
                ac.setId(roleMenuAssocSeq.nextValue());
                roleMenuAssocMapper.insert(ac);
            }
        }

    }

    @Override
    @Transactional
    public void unAssignMenuToRole(Role role, Menu menu) {

        Condition condition = new Condition(RoleMenuAssoc.class);
        condition.createCriteria().andEqualTo("role_code", role.getCode())
                .andEqualTo("menu_id", menu.getId());

        List<RoleMenuAssoc> assocs = roleMenuAssocMapper.selectByCondition(condition);

        for (RoleMenuAssoc assoc : assocs) {
            roleMenuAssocMapper.delete(assoc);
        }

    }

    /***
     * 删除角色到权限  角色到菜单  角色到用户  角色四个表中数据
     * @param roleCode
     */
    @Override
    @Transactional
    public void deleteRole(String roleCode) {
        Condition menuRoleAssoc = new Condition(RoleMenuAssoc.class);
        menuRoleAssoc.createCriteria().andEqualTo("roleCode", roleCode);
        roleMenuAssocMapper.deleteByCondition(menuRoleAssoc);

        Condition userRoleAssoc = new Condition(UserRoleAssoc.class);
        userRoleAssoc.createCriteria().andEqualTo("roleCode", roleCode);
        userRoleAssocMapper.deleteByCondition(userRoleAssoc);

        Condition roleAuthorityAssoc = new Condition(RoleAuthorityAssoc.class);
        roleAuthorityAssoc.createCriteria().andEqualTo("roleCode", roleCode);
        roleAuthorityAssocMapper.deleteByCondition(roleAuthorityAssoc);

        Condition roleCondition = new Condition(Role.class);
        roleCondition.createCriteria().andEqualTo("code", roleCode);
        baseMapper.deleteByCondition(roleCondition);
    }

    @Override
    @Transactional
    public void saveOrUpdate(Role role) {

        boolean newEntity = false;

        Condition condition = new Condition(Role.class);
        condition.createCriteria().andEqualTo("code", role.getCode());

        List<Role> oldRole = baseMapper.selectByCondition(condition);

        if (oldRole.size() == 0) {
            newEntity = true;
        }
        if (newEntity) {
            EntityUtils.setCreatedAudit(role);
            EntityUtils.setModifiedAudit(role);
            baseMapper.insert(role);
        } else {
            EntityUtils.setModifiedAudit(role);
            baseMapper.updateByConditionSelective(role, condition);
        }
    }

    public void defaultCreate() throws PanguException {

        Role admin = newEntity();
        try {
            admin.setCode("ROLE_ADMIN");
            admin.setName("管理员角色");
            admin.setDescription("管理员权限");

            EntityUtils.setCreatedAudit(admin);
            baseMapper.insert(admin);
        } catch (Exception ex) {
            ex.printStackTrace();
//            throw new PanguException(ex);
        }

        Role protectedUser = newEntity();
        try {

            protectedUser.setCode("ROLE_PROTECTED");
            protectedUser.setName("受保护用户角色");
            protectedUser.setDescription("受保护用户");

            EntityUtils.setCreatedAudit(protectedUser);

            baseMapper.insert(protectedUser);
        } catch (Exception ex) {
            ex.printStackTrace();
//            throw new PanguException(ex);
        }

        // 为管理员角色赋予所有权限
        List<Authority> allAuthorities = authorityMapper.selectAll();
        for (Authority authority : allAuthorities) {
            RoleAuthorityAssoc raa = new RoleAuthorityAssoc();
            raa.setId(roleAuthorityAssocSeq.nextValue());
            raa.setRoleCode(admin.getCode());
            raa.setAuthorityCode(authority.getCode());

            roleAuthorityAssocMapper.insertSelective(raa);
        }
    }


    @Override
    public List<Role> getRolesByUid(long uid) {

        return baseMapper.getRoleListByUser(uid);
    }


    @Override
    public List<RoleAuthorityAssoc> findRoleAuthorityAssocByCode(String roleCode) {
        List<RoleAuthorityAssoc> roleList = null;
        Condition condition = new Condition(RoleAuthorityAssoc.class);
        condition.createCriteria().andEqualTo("roleCode", roleCode);
        roleList = roleAuthorityAssocMapper.selectByCondition(condition);
        return roleList;
    }

    @Transactional
    @Override
    public void assignRoleToUser(List<RoleAuthorityAssoc> assocList) {
        // 先清除所有的关联，然后重新建立关联，假设参数里面的关联都是同一个角色的
        String roleCode = assocList.get(0).getRoleCode();
        Condition assocCondition = new Condition(RoleAuthorityAssoc.class);
        assocCondition.createCriteria().andEqualTo("roleCode", roleCode);

        roleAuthorityAssocMapper.deleteByCondition(assocCondition);


        if (assocList.get(0).getAuthorityCode() != null) {
            for (RoleAuthorityAssoc rac : assocList) {
                rac.setId(roleMenuAssocSeq.nextValue());
                roleAuthorityAssocMapper.insert(rac);
            }
        }
    }

}
