package com.grs.grs_client.controller;

import com.grs.api.model.NotificationDTO;
import com.grs.api.model.UserInformation;
import com.grs.core.service.ModelViewService;
import com.grs.core.service.NotificationService;
import com.grs.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private NotificationService notificationService;
    @Autowired
    private ModelViewService modelViewService;

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
