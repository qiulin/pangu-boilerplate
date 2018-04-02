package org.pf9.pangu.boilerplate.rest;

import org.pf9.pangu.boilerplate.ApplicationProperties;
import org.pf9.pangu.boilerplate.constants.APIRouteConstants;
import org.pf9.pangu.framework.common.config.PanguProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(APIRouteConstants.API_V1_ROOT + "/site")
public class SiteResource {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private PanguProperties huatuoProperties;

    @GetMapping("")
    public ResponseEntity info() {

        PanguProperties.Site site = huatuoProperties.getSite();

        return ResponseEntity.ok(site);
    }
}
