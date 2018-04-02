package org.pf9.pangu.boilerplate.rest;

import org.pf9.pangu.boilerplate.constants.APIRouteConstants;
import org.pf9.pangu.boilerplate.entity.Role;
import org.pf9.pangu.boilerplate.entity.RoleAuthorityAssoc;
import org.pf9.pangu.boilerplate.entity.RoleMenuAssoc;
import org.pf9.pangu.boilerplate.service.MenuService;
import org.pf9.pangu.boilerplate.service.RoleService;
import org.pf9.pangu.boilerplate.service.dto.MenuDTO;
import org.pf9.pangu.framework.common.exception.PanguException;
import org.pf9.pangu.framework.data.domain.ResponseResult;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RequestMapping(APIRouteConstants.API_V1_ROOT + "/role")
@RestController
public class RoleResource {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @GetMapping("table")
    public DataTablesResult<Role> table(@ModelAttribute @Valid DataTablesQuery query) {

        return roleService.getTable(query);
    }

    @GetMapping("init")
    public ResponseEntity init() {
        try {
            roleService.defaultCreate();
            return ResponseEntity.ok().body(ResponseResult.success());
        } catch (PanguException ex) {
            return ResponseEntity.badRequest().body(ex);
        }
    }

    @PostMapping("update")
    public int update(Role role) {
        return roleService.update(role);
    }

    @GetMapping("get")
    public ResponseEntity get(@RequestParam("code") String code) {

        Condition condition = new Condition(Role.class);
        condition.createCriteria().andEqualTo("code", code);
        List<Role> roleList = roleService.findByCondition(condition);

        if (roleList.size() > 0) {
            return ResponseEntity.ok().body(roleList.get(0));
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("menu/all")
    public ResponseEntity getMenu(@RequestParam(value = "role", required = false, defaultValue = "ROLE_NONE") String roleCode) {

//        List<MenuDTO> menuList = roleService.getAllMenuByRole(code);
        MenuDTO tree = menuService.getDTOTree(roleCode);

        return ResponseEntity.ok().body(tree);
    }

    @PostMapping("menu/assign")
    public ResponseResult assignMenuToRole(@RequestBody List<RoleMenuAssoc> assocList) {

        try {
            roleService.assignMenuToRole(assocList);
            return ResponseResult.success();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseResult.exception().data(ex);
        }
    }

    @PostMapping("delete")
    public ResponseResult deleteRole(@RequestBody HashMap params) {
        String roleCode = params.get("roleCode").toString();
        try {
            roleService.deleteRole(roleCode);
            return ResponseResult.success().message("删除角色成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseResult.exception().data(ex);
        }
    }

    @PostMapping("/save")
    public ResponseResult saveRole(@RequestBody Role role) {
        try {
            roleService.saveOrUpdate(role);
            return ResponseResult.success().message("保存角色成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseResult.exception().data(ex);
        }
    }


    @GetMapping("/getRoleAuthorityAssocs")
    public List<RoleAuthorityAssoc> getRoleAuthorityAssocs(@RequestParam(value = "roleCode") String roleCode) {
        return roleService.findRoleAuthorityAssocByCode(roleCode);
    }

    @PostMapping("/editUserRoleAssoc")
    public ResponseResult assignRoleToUser(@RequestBody List<RoleAuthorityAssoc> assocList) {

        try {
            roleService.assignRoleToUser(assocList);
            return ResponseResult.success();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseResult.exception().data(ex);
        }

    }

}
