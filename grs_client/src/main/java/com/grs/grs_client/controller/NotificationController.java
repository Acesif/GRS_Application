package com.grs.grs_client.controller;


import com.grs.grs_client.gateway.NotificationGateway;
import com.grs.grs_client.service.ModelAndViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
public class NotificationController {
    @Autowired
    private NotificationGateway notificationService;
    @Autowired
    private ModelAndViewService modelViewService;

    @RequestMapping(value = "/notifications.do", method = RequestMethod.GET)
    public ModelAndView getUsersNotifications(Authentication authentication, Model model, HttpServletRequest request){
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "notification",
                    "viewNotifications",
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }
}
