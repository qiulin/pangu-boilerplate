package org.pf9.pangu.boilerplate.rest;

import org.pf9.pangu.boilerplate.constants.APIRouteConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(APIRouteConstants.API_V1_ROOT + "/notify")
public class NotifyResource {

    @GetMapping("/pull")
    public ResponseEntity pullNotify() {

        return ResponseEntity.ok().build();

    }
}
