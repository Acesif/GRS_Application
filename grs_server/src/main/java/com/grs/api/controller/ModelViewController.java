package com.grs.api.controller;


import com.grs.core.service.ModelViewService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ModelViewController {

    private final ModelViewService modelViewService;

    //Get or Post
    @GetMapping(value = "/api/modelView/addNecessaryAttributesAndReturnViewPage")
    public ModelAndView addNecessaryAttributesAndReturnViewPage(
            Model model,
            Authentication authentication,
            HttpServletRequest request,
            //Is that ok?
            @RequestParam String mainPageFragmentName,
            @RequestParam String mainPageFragmentValue,
            @RequestParam String viewName) {
        return modelViewService.addNecessaryAttributesAndReturnViewPage(
                model,
                authentication,
                request,
                mainPageFragmentName,
                mainPageFragmentValue,
                viewName);

    }


    @GetMapping(value = "/api/modelView/returnViewsForNormalPages")
    public ModelAndView returnViewsForNormalPages(Authentication authentication,
                                                  Model model,
                                                  HttpServletRequest request,
                                                  @RequestParam String viewName) {

        return modelViewService.returnViewsForNormalPages(authentication, model, request, viewName);
    }
}
