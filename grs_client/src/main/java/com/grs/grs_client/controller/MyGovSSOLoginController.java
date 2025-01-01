package com.grs.grs_client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MyGovSSOLoginController {
//    @Autowired
//    private CellService cellService;
//    @Autowired
//    private EmployeeOfficeDAO employeeOfficeDAO;
//    @Autowired
//    private OISFUserDetailsServiceImpl userDetailsService;
//    @Autowired
//    private GrsRoleDAO grsRoleDAO;
//    @Autowired
//    private ESBConnectorService esbConnectorService;
//    @Autowired
//    private OfficesGroDAO officesGroDAO;
//    @Autowired
//    private CentralDashboardRecipientDAO centralDashboardRecipientDAO;
//    @Autowired
//    private FcmService fcmService;
//    @Autowired
//    private CellMemberDAO cellMemberDAO;
//    @Autowired
//    private ComplainantService complainantService;
//    @Autowired
//    private GrievanceService grievanceService;
//    @Autowired
//    private GrsRoleDAO roleDAO;
//    @Autowired
//    private Gson gson;
//    @Autowired
//    private LoginTraceService loginTraceService;
//    @Autowired
//    private RestTemplate restTemplate;
//    @Autowired
//    private MyGovConnectorService myGovConnectorService;
//
//
//    @RequestMapping(value = "/mygovlogout1", method = RequestMethod.GET)
//    public ModelAndView getLogoutPage() throws Exception {
//
//
//        IDP_Client idp = new IDP_Client(SSOPropertyReader.getInstance().getBaseUri() + Constant.myGovLogoutRedirectSuffix);
//        String url = idp.logoutRequest();
//        idp = null;
//        return new ModelAndView("redirect:" + url);
//    }
}
