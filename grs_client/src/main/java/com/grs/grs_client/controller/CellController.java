package com.grs.grs_client.controller;

import com.grs.grs_client.gateway.GeneralServiceGateway;
import com.grs.grs_client.gateway.OfficesGateway;
import com.grs.grs_client.service.ModelAndViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class CellController {
    @Autowired
    private ModelAndViewService modelViewService;
    @Autowired
    private GeneralServiceGateway generalSettingsService;

    @RequestMapping(value = "/viewMeetings.do", method = RequestMethod.GET)
    public ModelAndView getViewMeetingsPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            String fragmentFolder = "cell_meeting";
            String fragmentName = "viewMeetings";
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    fragmentFolder,
                    fragmentName,
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/addMeetings.do", method = RequestMethod.GET)
    public ModelAndView getAddMeetingsPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            String fragmentFolder = "cell_meeting";
            String fragmentName = "addMeetings";
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    fragmentFolder,
                    fragmentName,
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/viewMeetings.do", method = RequestMethod.GET, params = "id")
    public ModelAndView getViewMeetingDetailsPage(Authentication authentication, Model model, HttpServletRequest request, @RequestParam Long id) throws IllegalAccessException {
        if (authentication != null) {
            Integer maxFileSize = generalSettingsService.getMaximumFileSize();
            String allowedFileTypes = generalSettingsService.getAllowedFileTypes();
            model.addAttribute("maxFileSize", maxFileSize);
            model.addAttribute("allowedFileTypes", allowedFileTypes);
            model.addAttribute("fileSizeLabel", generalSettingsService.getAllowedFileSizeLabel());
            model.addAttribute("fileTypesLabel", generalSettingsService.getAllowedFileTypesLabel());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "cell_meeting",
                    "viewMeetingsDetails",
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }
}
