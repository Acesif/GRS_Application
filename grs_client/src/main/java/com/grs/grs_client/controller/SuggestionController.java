package com.grs.grs_client.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by User on 9/27/2017.
 */

@Slf4j
@RestController
//TODO backend checking of field of suggestion form
public class SuggestionController {
    @Autowired
    private SuggestionService suggestionService;
    @Autowired
    private ModelViewService modelViewService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private ComplainantService complainantService;
    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/suggestion.do", method = RequestMethod.GET)
    public ModelAndView suggestionRequest(Authentication authentication, HttpServletRequest request, Model model) {
        if(authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String name = null, phone = null, email = null;
            if(userInformation.getUserType().equals(UserType.COMPLAINANT)) {
                Complainant complainant = complainantService.findOne(userInformation.getUserId());
                name = complainant.getName();
                phone = complainant.getPhoneNumber();
                email = complainant.getEmail();
            } else if(userInformation.getUserType().equals(UserType.OISF_USER)) {
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                EmployeeRecord employeeRecord = officeService.findEmployeeRecordById(officeInformation.getEmployeeRecordId());
                name = messageService.isCurrentLanguageInEnglish() ? employeeRecord.getNameEnglish() : employeeRecord.getNameBangla();
                phone = employeeRecord.getPersonalMobile();
                email = employeeRecord.getPersonalEmail();
            }
            model.addAttribute("name", name);
            model.addAttribute("phone", phone);
            model.addAttribute("email", email);
        }
        return modelViewService.returnViewsForNormalPages(authentication, model, request, "suggestion");
    }

    @RequestMapping(value = "viewSuggestions.do", method = RequestMethod.GET)
    public ModelAndView getSuggestionsByUser(Authentication authentication,
                                             Model model, HttpServletRequest request,
                                             @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {

        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            OfficeInformation officeInformation = userInformation.getOfficeInformation();
            String requestParams = request.getParameter("params");
            Long officeId = officeInformation.getOfficeId();
            String officeName = messageService.isCurrentLanguageInEnglish() ? officeInformation.getOfficeNameEnglish() : officeInformation.getOfficeNameBangla();
            Boolean isDrilledDown = false;
            if(StringUtil.isValidString(requestParams)) {
                String decodedParams = StringUtils.newStringUtf8(Base64.decodeBase64(requestParams.substring(20)));
                Long childOfficeId = Long.parseLong(decodedParams);
                Office childOffice = officeService.findOne(childOfficeId);
                List<Long> parentOfficeIds = officeService.getAncestorOfficeIds(childOfficeId);
                if(childOffice != null && (parentOfficeIds.contains(officeId) || userInformation.getIsCentralDashboardUser())) {
                    officeId = childOfficeId;
                    officeName = messageService.isCurrentLanguageInEnglish() ? childOffice.getNameEnglish() : childOffice.getNameBangla();
                    isDrilledDown = true;
                } else {
                    return new ModelAndView("redirect:/error-page");
                }
            }
            model.addAttribute("officeId", officeId);
            model.addAttribute("officeName", officeName);
            model.addAttribute("isDrilledDown", isDrilledDown);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "suggestion",
                    "viewSuggestions",
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");

    }
}
