package com.grs.grs_client.controller;

import com.grs.grs_client.common.IDP_Client;
import com.grs.grs_client.common.SSOPropertyReader;
import com.grs.grs_client.utils.Constant;
import com.grs.grs_client.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class MyGovSSOLoginController {

    @RequestMapping(value = "/mygovlogout1", method = RequestMethod.GET)
    public ModelAndView getLogoutPage() throws Exception {

        IDP_Client idp = new IDP_Client(SSOPropertyReader.getInstance().getBaseUri() + Constant.myGovLogoutRedirectSuffix);
        String url = idp.logoutRequest();
        return new ModelAndView("redirect:" + url);
    }

    @RequestMapping(value = "/ssologout", method = RequestMethod.GET)
    public void ssologout(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getSession().invalidate();
            CookieUtil.clear(response, Constant.HEADER_STRING);
            response.sendRedirect("/");
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
