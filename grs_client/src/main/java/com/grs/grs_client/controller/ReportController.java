package com.grs.grs_client.controller;


import com.grs.grs_client.service.ModelAndViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ReportController {

    @Autowired
    private ModelAndViewService modelViewService;

    @RequestMapping(value = "/viewFieldCoordination.do", method = RequestMethod.GET)
    public ModelAndView getFieldCoordinationPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication == null) {
            return new ModelAndView("redirect:/error-page");
        }
        return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                authentication,
                request,
                "reports",
                "fieldCoordination",
                "admin"
        );
    }
}
