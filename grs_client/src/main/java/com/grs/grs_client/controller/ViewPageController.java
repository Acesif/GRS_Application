package com.grs.grs_client.controller;

import com.grs.grs_client.service.ModelAndViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

}
