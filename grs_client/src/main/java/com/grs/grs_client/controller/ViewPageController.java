package com.grs.grs_client.controller;

import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.service.ModelAndViewService;
import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class ViewPageController {

    @Autowired
    private ModelAndViewService modelViewService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView firstPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        return modelViewService.returnViewsForNormalPages(model, request, "index");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLoginPage(Authentication authentication) {
        if (authentication == null) {
            return new ModelAndView("redirect:/login?a=0");
        } else {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if (userInformation.getUserType().equals(UserType.OISF_USER) &&
                    (userInformation.getOisfUserType().equals(OISFUserType.GRO) || userInformation.getOisfUserType().equals(OISFUserType.SERVICE_OFFICER))) {
                return new ModelAndView("redirect:/viewGrievances.do");

            } else {
                return new ModelAndView("redirect:/dashboard.do");
            }
        }
    }

}
