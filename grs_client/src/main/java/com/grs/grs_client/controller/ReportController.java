package com.grs.grs_client.controller;

import com.grs.api.model.*;
import com.grs.api.model.response.GenericResponse;
import com.grs.api.model.response.grievance.GrievanceComplainantInfoDTO;
import com.grs.api.model.response.reports.GrievanceAndAppealDailyReportDTO;
import com.grs.api.model.response.reports.GrievanceAndAppealMonthlyReportDTO;
import com.grs.api.model.response.reports.GrievanceMonthlyReportsDTO;
import com.grs.core.service.ModelViewService;
import com.grs.core.service.ReportsService;
import com.grs.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.List;

@Slf4j
@RestController
public class ReportController {
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private ModelViewService modelViewService;

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
