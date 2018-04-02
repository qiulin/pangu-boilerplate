package org.pf9.pangu.boilerplate.rest;


import org.pf9.pangu.boilerplate.entity.*;
import org.pf9.pangu.boilerplate.service.GroupService;
import org.pf9.pangu.boilerplate.service.dto.GroupDto;
import org.pf9.pangu.framework.data.domain.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group")
public class GroupResource {

    @Autowired
    private GroupService groupService;

    private Logger log = LoggerFactory.getLogger(GroupResource.class);


    @GetMapping("tree")
    public ResponseEntity<GroupDto> treeMenu() {

        GroupDto tree = groupService.getTree();
        log.debug(tree.toString());
        return ResponseEntity.ok().body(tree);
    }

    @GetMapping("getGroupUsers")
    public ResponseEntity getGroupUsers(@RequestParam(value="code")String code) {

        List<User> users = groupService.getUserByGroup(code);
        return ResponseEntity.ok().body(users);
    }


    @PostMapping("saveOrUpdate")
    public ResponseEntity save(@RequestBody Group group) {


        try {
            Group groupReturn = groupService.saveOrUpdateGroup(group);
            return ResponseEntity.ok().body(ResponseResult.success().data(groupReturn));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(ResponseResult.failure().message("保存用户组失败"));
        }
    }

    @PostMapping("assignUserToGroup")
    public ResponseEntity assignUserToGroup(@RequestBody List<UserGroupAssoc> assocs){
        try{
            groupService.assignUserToGroup(assocs);
            return ResponseEntity.ok().body(ResponseResult.success());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseResult.failure().message("操作失败"));
        }



    }
}
