package org.pf9.pangu.boilerplate.rest;


import org.pf9.pangu.boilerplate.constants.APIRouteConstants;
import org.pf9.pangu.boilerplate.entity.Authority;
import org.pf9.pangu.boilerplate.service.AuthorityService;
import org.pf9.pangu.framework.data.domain.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping(APIRouteConstants.API_V1_ROOT + "/authority")
public class AuthorityResource {

    @Autowired
    private AuthorityService authorityService;

    @PostMapping("/add")
    public ResponseResult add(@RequestBody Authority authority) {
        authority.setCreatedDate(Instant.now());
        authorityService.addAuthority(authority);
        return ResponseResult.success();
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody Authority authority) {
        authority.setLastModifiedDate(Instant.now());
        authorityService.update(authority);
        return ResponseResult.success();
    }


    @DeleteMapping(value = "/")
    public ResponseResult deleteAuthority(@RequestParam(value = "code") String code) {
        authorityService.deleteAuthorityById(code);
        return ResponseResult.success();

    }

    @PostMapping("/delete")
    public ResponseResult delete(@RequestBody Authority authority) {
        authorityService.delete(authority);
        return ResponseResult.success();
    }


    @GetMapping("/{id}")
    public Authority findById(@PathParam("id") Long id) {
        Authority u = new Authority();
        Optional<Authority> authority = authorityService.findById(id);
        if (authority.isPresent()) {
            u = authority.get();
        }
        return u;
    }
}
