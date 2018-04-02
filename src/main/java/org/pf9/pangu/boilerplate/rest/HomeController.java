package org.pf9.pangu.boilerplate.rest;


import org.pf9.pangu.boilerplate.ApplicationProperties;
import org.pf9.pangu.boilerplate.service.AuthorityService;
import org.pf9.pangu.boilerplate.service.MenuService;
import org.pf9.pangu.boilerplate.service.RoleService;
import org.pf9.pangu.boilerplate.service.UserService;
import org.pf9.pangu.framework.data.domain.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @GetMapping("initdb")
    @ResponseBody
    public ResponseResult initdb() {

        try {
            menuService.defaultCreate();    // 根据 json 配置生成所有菜单
            authorityService.defaultCreate(); // 根据注解生成所有权限
            roleService.defaultCreate(); // 创建管理员角色、权限
            userService.defaultCreate();  // 创建管理员用户

            return ResponseResult.success().message("初始化成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseResult.exception().data(ex);
        }
    }
}
