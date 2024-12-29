package com.grs.grs_client.controller;

import com.grs.grs_client.gateway.OfficesGateway;
import com.grs.grs_client.model.RoleContainerDTO;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OfficeController {

    @Autowired
    private OfficesGateway officeService;

    @RequestMapping(value = "/api/user/organograms", method = RequestMethod.GET)
    public RoleContainerDTO getOrganogramsForLoggedInUser(Authentication authentication) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return officeService.getOfficeUnitOrganogramsForLoggedInUser(userInformation);
    }
}
