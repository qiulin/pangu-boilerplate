package org.pf9.pangu.boilerplate.service;

import org.pf9.pangu.boilerplate.entity.Menu;
import org.pf9.pangu.boilerplate.entity.Role;
import org.pf9.pangu.boilerplate.entity.RoleAuthorityAssoc;
import org.pf9.pangu.boilerplate.entity.RoleMenuAssoc;
import org.pf9.pangu.boilerplate.mapper.RoleMapper;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.pf9.pangu.framework.data.service.CreateDefaultService;

import java.util.List;

public interface RoleService extends CreateDefaultService, EntityService<Role, RoleMapper> {

    DataTablesResult<Role> getTable(DataTablesQuery query);

    List<Role> getRolesByUid(long uid);

    void assignMenuToRole(Role role, Menu menu);

    void unAssignMenuToRole(Role role, Menu menu);

    void assignMenuToRole(List<RoleMenuAssoc> assocList);

    void deleteRole(String roleCode);

    void saveOrUpdate(Role role);

    List<RoleAuthorityAssoc> findRoleAuthorityAssocByCode(String roleCode);

    void assignRoleToUser(List<RoleAuthorityAssoc> assocList);
}
