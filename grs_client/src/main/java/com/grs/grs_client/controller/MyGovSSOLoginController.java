package com.grs.grs_client.controller;

import com.grs.grs_client.common.IDP_Client;
import com.grs.grs_client.common.SSOPropertyReader;
import com.grs.grs_client.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestController
public class MyGovSSOLoginController {

    @RequestMapping(value = "/mygovlogout1", method = RequestMethod.GET)
    public ModelAndView getLogoutPage() throws Exception {

        IDP_Client idp = new IDP_Client(SSOPropertyReader.getInstance().getBaseUri() + Constant.myGovLogoutRedirectSuffix);
        String url = idp.logoutRequest();
        return new ModelAndView("redirect:" + url);
    }
}
