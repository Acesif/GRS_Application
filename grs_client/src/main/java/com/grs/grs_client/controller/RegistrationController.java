package com.grs.grs_client.controller;

import com.grs.grs_client.service.ModelAndViewService;
import com.grs.grs_client.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by Acer on 9/19/2017.
 */
@Slf4j
@RestController
public class RegistrationController {
    @Autowired
    private ModelAndViewService modelViewService;

    @RequestMapping(value = "/register.do", method = RequestMethod.GET)
    public ModelAndView registrationPage(HttpServletRequest request, Model model) {
        return modelViewService.returnViewsForNormalPages(model, request, "grsRegistrationForm");
    }

    @RequestMapping(value = "/mobileRegistration.do", method = RequestMethod.GET)
    public ModelAndView mobileRegistrationPage(HttpServletRequest request, Authentication authentication, Model model){
        String languageCode = CookieUtil.getValue(request, "lang");
        model.addAttribute("lang", languageCode);
        model.addAttribute("isLoggedIn", false);
        model.addAttribute("isGrsUser", false);
        model.addAttribute("isOthersComplainant", false);
        model.addAttribute("isOisfUser", false);
        model.addAttribute("isMobileLogin", true);
        return new ModelAndView( "grsRegistrationForm");
    }

    @RequestMapping(value = "/viewBlacklist.do", method = RequestMethod.GET)
    public ModelAndView getBlacklistPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "blacklist",
                    "viewBlacklist",
                    "admin"
            );
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/viewBlacklistRequests.do", method = RequestMethod.GET)
    public ModelAndView getBlacklistRequestPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "blacklist",
                    "viewRequestedBlacklist",
                    "admin"
            );
        }
        return new ModelAndView("redirect:/error-page");
    }
}
