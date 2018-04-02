package org.pf9.pangu.boilerplate.rest;

import org.pf9.pangu.boilerplate.constants.APIRouteConstants;
import org.pf9.pangu.boilerplate.entity.Authority;
import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.entity.UserRoleAssoc;
import org.pf9.pangu.boilerplate.service.AuthorityService;
import org.pf9.pangu.boilerplate.service.RoleService;
import org.pf9.pangu.boilerplate.service.UserService;
import org.pf9.pangu.boilerplate.util.EntityUtils;
import org.pf9.pangu.framework.auth.security.SecurityUtils;
import org.pf9.pangu.framework.data.domain.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(APIRouteConstants.API_V1_ROOT + "/user")
public class UserResource {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthorityService authorityService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> findById(@PathVariable(value = "userId") long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }


    @GetMapping("/admin")
    public ResponseResult getAdminUsers() {
        List<User> users = null;
        try {
            users = userService.getAdminUsers();
        } catch (Exception e) {
            return ResponseResult.failure();
        }
        return ResponseResult.success().data(users);
    }

    @GetMapping("/my")
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/")
    public ResponseEntity getAllUser() {
        List<User> userList = userService.findAll();
        return ResponseEntity.ok().body(userList);
    }

    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public ResponseEntity addUser(@RequestBody User user) {
        try {
            if (!EntityUtils.checkPassword(user.getPassword()).equals("弱")) {
                int checkResult = userService.findLogin(user.getLogin());
                if (checkResult != 0) {
                    return ResponseEntity.ok().body(ResponseResult.failure().message("用户名已存在"));
                }
                User saveUser = userService.createUser(user);
                if (saveUser != null) {
                    return ResponseEntity.ok().body(ResponseResult.success().message("保存成功").data(saveUser));
                }
            }
            return ResponseEntity.ok().body(ResponseResult.failure().message("密码强度太弱，请重设密码"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(ResponseResult.failure().message("用户保存异常"));

        }

    }

    /**
     * 修改user，当前登陆User
     *
     * @param user
     * @return
     */
    @PostMapping(value = "/edit")
    @ResponseBody
    public int editUser(@RequestBody User user) {
        if (userService.updateUser(user) != null) {
            return 1;
        }
        return 0;
    }

    @DeleteMapping(value = "/{uid}")
    public ResponseEntity deleteUser(@PathVariable(value = "uid") long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    public List<UserRoleAssoc> getUserRoles(@RequestParam(value = "uid") long userId) {
        return userService.findUserRoleAssocByUid(userId);
    }

    @PostMapping("/roles")
    public ResponseResult assignRoleToUser(@RequestBody List<UserRoleAssoc> assocList) {

        try {
            userService.assignRoleToUser(assocList);
            return ResponseResult.success();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseResult.exception().data(ex);
        }

    }

    @GetMapping("/my/authorities")
    public ResponseEntity myAuthorities() {

        String login = SecurityUtils.getCurrentUserLogin();
        Optional<User> _me = userService.findUserByLogin(login);

        if (_me.isPresent()) {
            List<Authority> authorities = authorityService.findAuthoritiesByUserId(_me
                    .get().getId());

            return ResponseEntity.ok(authorities);

        }
        return ResponseEntity.noContent().build();
    }
}
