package com.grs.grs_client.controller;
import com.grs.grs_client.common.IDP_Client;
import com.grs.grs_client.common.SSOPropertyReader;
import com.grs.grs_client.config.GrantedAuthorityImpl;
import com.grs.grs_client.config.TokenAuthenticationServiceUtil;
import com.grs.grs_client.config.UserDetailsImpl;
import com.grs.grs_client.domain.RedirectMap;
import com.grs.grs_client.enums.GRSUserType;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.ServiceType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.gateway.*;
import com.grs.grs_client.model.*;
import com.grs.grs_client.model.BaseObjectDTO.BaseObjectDTO;
import com.grs.grs_client.service.ModelAndViewService;
import com.grs.grs_client.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ViewPageController {

    private final ModelAndViewService modelViewService;
    private final OfficesGateway officeService;
    private final AuthGateway authGateway;
    private final MessageGateway messageService;
    private final ComplainantGateway complainantService;
    private final CitizenCharterGateway citizenCharterService;
    private final SpProgrammeGateway spProgrammeService;
    private final EducationGateway educationService;
    private final EmailSmsSettingsGateway emailSmsSettingsService;
    private final PermissionsToUsersGateway permissionsToUsersDAO;
    private final GrsUsergateway grsUserService;
    private final EmailGateway emailService;
    private final ShortMessageGateway shortMessageService;
    private final OccupationGateway occupationService;
    private final GeneralServiceGateway generalSettingsService;
    private final OfficesGroGateway officesGroDAO;


    @Value("${app.base.url:''}")
    private String appBaseUrl;
    @Value("${idp.url:''}")
    private String idpUrl;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView firstPage(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(required = false) String data
    ) {

        if (data != null) {

            try {
                LoginResponse loginResponse = authGateway.adminLogin(data);
//                loginResponse.getUserInformation().setToken(loginResponse.getAccessToken());
                List<GrantedAuthorityImpl> authorities = loginResponse.getAuthorities().stream()
                        .map(GrantedAuthorityImpl::new)
                        .collect(Collectors.toList());

                UserDetailsImpl userDetails = UserDetailsImpl.builder()
                        .username(loginResponse.getUserInformation().getUsername())
                        .isAccountAuthenticated(true)
                        .grantedAuthorities(authorities)
                        .userInformation(loginResponse.getUserInformation())
                        .accessToken(loginResponse.getAccessToken())
                        .build();

                TokenAuthenticationServiceUtil.addAuthenticationForMyGov(userDetails, request, response);

            } catch (Exception e) {
                log.error("Message:", e);
            }
        }

        return modelViewService.returnViewsForNormalPages(authentication, model, request, "index");
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

    @RequestMapping(value = "/login", method = RequestMethod.GET, params = "a")
    public ModelAndView getLoginPage(HttpServletRequest request,
                                     Model model,
                                     @RequestParam String a,
                                     @RequestParam(value = "", defaultValue = "", required = false) String redirectUrl,
                                     @RequestParam(value = "", defaultValue = "", required = false) String phoneNumber,
                                     Authentication authentication,
                                     @CookieValue(value = "prev_url", defaultValue = "") String previousUrl) throws Exception {

        SubMenuDTO subMenuUsernameDTO, subMenuPasswordDTO, formTitleDTO;
        RedirectMap redirectMap = RedirectMap.DASHBOARD;
        if (StringUtil.isValidString(previousUrl)) {
            //log.info("previous url from cookie: " + previousUrl);
            redirectMap = RedirectMap.get(previousUrl);

            request.getSession().setAttribute("prev_url", previousUrl);
            //log.info(request.getSession().getAttribute("prev_url").toString());

        }

        if (a.compareTo("1") == 0) {
            try {
                String redirectURL = idpUrl + Utility.toBase64(appBaseUrl);
                return new ModelAndView("redirect:" + redirectURL);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if (userInformation.getUserType().equals(UserType.OISF_USER) &&
                    (userInformation.getOisfUserType().equals(OISFUserType.GRO) || userInformation.getOisfUserType().equals(OISFUserType.SERVICE_OFFICER))) {
                return new ModelAndView("redirect:/viewGrievances.do");

            } else {
                return new ModelAndView("redirect:/viewGrievances.do");
            }
        }

        model.addAttribute("userNameText", "login");
        if (a.equals("1")) {
            subMenuUsernameDTO = SubMenuDTO.builder().nameEnglish("Username").nameBangla("ইউজারনেম").link("javascript:;").build();
            subMenuPasswordDTO = SubMenuDTO.builder().nameEnglish("Password").nameBangla("পাসওয়ার্ড").link("javascript:;").build();
            formTitleDTO = SubMenuDTO.builder().nameEnglish("Administrative login").nameBangla("প্রশাসনিক লগইন").link("javascript:;").build();
        } else if (a.equals("5")) {
            IDP_Client idp = new IDP_Client(SSOPropertyReader.getInstance().getBaseUri() + Constant.myGovLoginRedirectSuffix);
            String url = idp.loginRequest2();
            idp = null;
            return new ModelAndView("redirect:" + url);
        } else {
            subMenuUsernameDTO = SubMenuDTO.builder().nameEnglish("Mobile number (in Bangla/English)").nameBangla("মোবাইল নম্বর (বাংলায়/ইংরেজিতে)").link("javascript:;").build();
            subMenuPasswordDTO = SubMenuDTO.builder().nameEnglish("Pin Code (in English)").nameBangla("পিনকোড (ইংরেজিতে)").link("javascript:;").build();
            formTitleDTO = SubMenuDTO.builder().nameEnglish("Complainant Login").nameBangla("অভিযোগকারী লগইন").link("javascript:;").build();
        }
        String languageCode = CookieUtil.getValue(request, "lang");
        model.addAttribute("lang", languageCode);
        model.addAttribute("usernameFieldText", subMenuUsernameDTO);
        model.addAttribute("passwordFieldText", subMenuPasswordDTO);
        model.addAttribute("formTitleText", formTitleDTO);
        model.addAttribute("valueOfA", a);
        model.addAttribute("redirectUrl", redirectUrl);
        model.addAttribute("phoneNumber", phoneNumber);
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/login/success", method = RequestMethod.POST)
    public void redirectAfterLoginSuccessPOST(HttpServletResponse response, HttpServletRequest request, Authentication authentication) throws IOException {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            boolean isOthersComplainant = Utility.isUserAnOthersComplainant(userInformation);
            String lastSavedUrl = (String) request.getSession(false).getAttribute("prev_url");
            //log.info(lastSavedUrl);
            if (userInformation.getUserType().equals(UserType.OISF_USER)) {
                if ((StringUtil.isValidString(lastSavedUrl) && lastSavedUrl.contains("/addStaffGrievances.do"))) {
                    request.getSession().setAttribute("prev_url", "");
                    response.sendRedirect((lastSavedUrl));
                } else {
                    if (userInformation.getOisfUserType().equals(OISFUserType.HEAD_OF_OFFICE)) {
                        response.sendRedirect("/dashboard.do");
                    } else {
                        response.sendRedirect("/viewGrievances.do");
                    }
                }
            } else if (userInformation.getUserType().equals(UserType.COMPLAINANT) || isOthersComplainant) {
                if ((StringUtil.isValidString(lastSavedUrl)
                        && (lastSavedUrl.contains("/addPublicGrievances.do")
                        || lastSavedUrl.contains("/complainForOthers.do")))) {
                    request.getSession().setAttribute("prev_url", "");
                    response.sendRedirect(lastSavedUrl);
                } else {
                    response.sendRedirect("/viewGrievances.do");
                }
            } else if (userInformation.getUserType().equals(UserType.SYSTEM_USER)) {
                response.sendRedirect("/viewOffice.do");
            }
        } else {
            response.sendRedirect("/");
        }
    }

    @RequestMapping(value = "/login/success", method = RequestMethod.GET)
    public void redirectAfterLoginSuccess(HttpServletResponse response, HttpServletRequest request, Authentication authentication) throws IOException {
        //log.info("/login/success : authentication: {}",authentication);

        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            //log.info("userInformation: {}",userInformation);
            boolean isOthersComplainant = Utility.isUserAnOthersComplainant(userInformation);
            String lastSavedUrl = (String) request.getSession(false).getAttribute("prev_url");
            //log.info(lastSavedUrl);
            if (userInformation.getUserType().equals(UserType.OISF_USER)) {
                if ((StringUtil.isValidString(lastSavedUrl) && lastSavedUrl.contains("/addStaffGrievances.do"))) {
                    request.getSession().setAttribute("prev_url", "");
                    response.sendRedirect((lastSavedUrl));
                } else {
                    if (userInformation.getOisfUserType().equals(OISFUserType.HEAD_OF_OFFICE)) {
                        response.sendRedirect("/dashboard.do");
                    } else {
                        response.sendRedirect("/viewGrievances.do");
                    }
                }
            } else if (userInformation.getUserType().equals(UserType.COMPLAINANT) || isOthersComplainant) {
                if ((StringUtil.isValidString(lastSavedUrl)
                        && (lastSavedUrl.contains("/addPublicGrievances.do")
                        || lastSavedUrl.contains("/complainForOthers.do")))) {
                    request.getSession().setAttribute("prev_url", "");
                    response.sendRedirect(lastSavedUrl);
                } else {
                    response.sendRedirect("/viewGrievances.do");
                }
            } else if (userInformation.getUserType().equals(UserType.SYSTEM_USER)) {
                response.sendRedirect("/viewOffice.do");
            }
        } else {
            response.sendRedirect("/");
        }
    }

    @RequestMapping(value = "/services.do", method = RequestMethod.GET)
    public ModelAndView servicesPage(HttpServletRequest request, Authentication authentication, Model model) {
        model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
        return modelViewService.returnViewsForNormalPages(authentication, model, request, "services");
    }

    @RequestMapping(value = "/groInformation.do", method = RequestMethod.GET)
    public ModelAndView groInformationPage(HttpServletRequest request, Authentication authentication, Model model) {
        model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
        return modelViewService.returnViewsForNormalPages(authentication, model, request, "groInformation");
    }

    @RequestMapping(value = "/dashboardMobile.do", method = RequestMethod.GET)
    public ModelAndView getDashboardPage(HttpServletRequest request, Authentication authentication, Model model) {
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        String fragmentName = "";
        String viewName = "admin";
        if (userInformation.getUserType().equals(UserType.COMPLAINANT)) {
            Boolean isBlacklisted = complainantService.isBlacklistedUser(userInformation.getUserId());
            model.addAttribute("isBlacklisted", isBlacklisted);
            fragmentName = "dashboardCitizen";
        } else if (userInformation.getUserType().equals(UserType.OISF_USER)) {
            if (userInformation.getOisfUserType().equals(OISFUserType.GRO) || userInformation.getOisfUserType().equals(OISFUserType.HEAD_OF_OFFICE) || userInformation.getIsCentralDashboardUser()) {
                String requestParams = request.getParameter("params");
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                Long officeId = officeInformation.getOfficeId();
                String officeName = messageService.isCurrentLanguageInEnglish() ? officeInformation.getOfficeNameEnglish() : officeInformation.getOfficeNameBangla();
                boolean isDrilledDown = false;
                if (StringUtil.isValidString(requestParams)) {
                    String decodedParams = StringUtils.newStringUtf8(Base64.decodeBase64(requestParams.substring(20)));
                    Long childOfficeId = Long.parseLong(decodedParams);
                    Office childOffice = officeService.findOne(childOfficeId);
                    List<Long> parentOfficeIds = officeService.getAncestorOfficeIds(childOfficeId);
                    if (childOffice != null && (parentOfficeIds.contains(officeId) || userInformation.getIsCentralDashboardUser())) {
                        officeId = childOfficeId;
                        officeName = messageService.isCurrentLanguageInEnglish() ? childOffice.getNameEnglish() : childOffice.getNameBangla();
                        isDrilledDown = true;
                    } else {
                        return new ModelAndView("redirect:/error-page");
                    }
                }
                model.addAttribute("officeId", officeId);
                model.addAttribute("officeName", officeName);
                model.addAttribute("currentMonthYear", messageService.getCurrentMonthYearAsString());
                model.addAttribute("isDrilledDown", isDrilledDown);
                model.addAttribute("canViewAppealAndSubOfficeDashboard", officeService.hasAccessToAoAndSubOfficesDashboard(officeId));
                fragmentName = "dashboardGro";
            } else if (userInformation.getOisfUserType().equals(OISFUserType.SUPER_ADMIN)) {
                fragmentName = "dashboardSuperAdmin";
                viewName = "superAdmin";
            } else {
                fragmentName = "dashboard";
            }
        } else if (userInformation.getUserType().equals(UserType.SYSTEM_USER)) {
            if (userInformation.getGrsUserType().equals(GRSUserType.SUPER_ADMIN)) {
                return new ModelAndView("redirect:/viewOffice.do");
            }
        }
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "dashboard",
                    fragmentName,
                    viewName);
        } else {
            return new ModelAndView("redirect:/error-page");
        }
    }

    @RequestMapping(value = "/centralDashboard.do", method = RequestMethod.GET)
    public ModelAndView getCentralDashboardPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if (userInformation.getIsCentralDashboardUser()) {
                model.addAttribute("currentMonthYear", messageService.getCurrentMonthYearAsString());
                return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                        authentication,
                        request,
                        "dashboard",
                        "dashboardCentral",
                        "admin");
            }
        }
        return new ModelAndView("redirect:/error-page");
    }

     /**
            The controllers below are done by Md. Musfikur Hasan Oli
     **/

    @RequestMapping(value = "/viewRegister.do", method = RequestMethod.GET)
    public ModelAndView getRegisterPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            Long officeId = userInformation.getOfficeInformation().getOfficeId();
            String requestParams = request.getParameter("params");
            if (StringUtil.isValidString(requestParams)) {
                Long officeIdParam = StringUtil.decodeOfficeIdOnDashboardDrillDown(requestParams);
                Office office = officeService.getOfficeByOfficeId(officeIdParam);
                if (office != null) {
                    officeId = officeIdParam;
                }
            }
            model.addAttribute("officeId", officeId);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "register",
                    "viewRegister",
                    "admin"
            );
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/viewAppealRegister.do", method = RequestMethod.GET)
    public ModelAndView getAppealRegisterPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            Long officeId = userInformation.getOfficeInformation().getOfficeId();
            model.addAttribute("officeId", officeId);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "register",
                    "viewAppealRegister",
                    "admin"
            );
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/viewReports.do", method = RequestMethod.GET)
    public ModelAndView getReportPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            OfficeInformation officeInformation = userInformation.getOfficeInformation();
            String requestParams = request.getParameter("params");
            Long officeId = officeInformation.getOfficeId();
            String officeName = messageService.isCurrentLanguageInEnglish() ? officeInformation.getOfficeNameEnglish() : officeInformation.getOfficeNameBangla();
            boolean isDrilledDown = false;
            if (StringUtil.isValidString(requestParams)) {
                String decodedParams = StringUtils.newStringUtf8(Base64.decodeBase64(requestParams.substring(20)));
                Long childOfficeId = Long.parseLong(decodedParams);
                Office childOffice = officeService.findOne(childOfficeId);
                List<Long> parentOfficeIds = officeService.getAncestorOfficeIds(childOfficeId);
                if (childOffice != null && (parentOfficeIds.contains(officeId) || userInformation.getIsCentralDashboardUser())) {
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
                    "reports",
                    "viewReports",
                    "admin"
            );
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewCitizenCharter.do", method = RequestMethod.GET)
    public ModelAndView getViewCitizenCharterPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "citizencharter",
                    "viewCitizenCharter",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "editCitizenCharter.do", method = RequestMethod.GET)
    public ModelAndView getEditCitizenCharterPage(HttpServletRequest request, Authentication authentication, Model model, @RequestParam(value = "id") Long id) {
        if (authentication != null) {
            CitizenCharter citizenCharterDTO = citizenCharterService.getCitizenCharterByOfficeCitizenCharterId(id);
            model.addAttribute("service", citizenCharterDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "citizencharter",
                    "addCitizenCharter",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "addCitizenCharter.do", method = RequestMethod.GET)
    public ModelAndView getAddCitizenCharterPage(HttpServletRequest httpServletRequest, Authentication authentication, Model model) {
        WeakHashMap<String, ServiceType> serviceTypes = new WeakHashMap<>();
        serviceTypes.put("citizen", ServiceType.NAGORIK);
        serviceTypes.put("official", ServiceType.DAPTORIK);
        serviceTypes.put("internal", ServiceType.STAFF);
        if (authentication != null) {
            model.addAttribute("serviceTypes", serviceTypes);
            CitizenCharter citizenCharterDTO = new CitizenCharter();
            model.addAttribute("service", citizenCharterDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "citizencharter",
                    "addCitizenCharter",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "addCitizenCharterOrigin.do", method = RequestMethod.GET)
    public ModelAndView getAddCitizenCharterOriginPage(HttpServletRequest request,
                                                       Authentication authentication,
                                                       Model model,
                                                       @RequestParam("officeOriginId") Long officeOriginId,
                                                       @RequestParam("id") Long id) {
        id = (id != null) ? id : 0L;
        if (authentication != null) {
            WeakHashMap<String, ServiceType> serviceTypes = new WeakHashMap<>();
            serviceTypes.put("citizen", ServiceType.NAGORIK);
            serviceTypes.put("official", ServiceType.DAPTORIK);
            serviceTypes.put("internal", ServiceType.STAFF);

            ServiceOrigin serviceOriginDTO = officeService.getServiceOriginDTObyId(id);
            List<OfficeOriginUnitDTO> officeOriginUnitList = officeService.getOfficeOriginUnitDTOListByOfficeOriginId(officeOriginId);
            Long officeOriginUnitOrganogramId = serviceOriginDTO.getOfficeOriginUnitOrganogramId();
            if (officeOriginUnitOrganogramId != null && officeOriginUnitOrganogramId > 0) {
                List<OfficeOriginUnitOrganogramDTO> officeOriginUnitOrganogramList = officeService.getOfficeOriginUnitOrganogramDTOListByOfficeOriginUnitId(serviceOriginDTO.getOfficeOriginUnitId());
                if(officeOriginUnitOrganogramList != null && !officeOriginUnitOrganogramList.isEmpty()) {
//                    for (int i = 0 ;i<officeOriginUnitOrganogramList.size() ;i++) {
//                        if(officeOriginUnitOrganogramList.get(i).getNameBangla() == null || officeOriginUnitOrganogramList.get(i).getNameBangla().length() ==0) {
//                            officeOriginUnitOrganogramList.get(i).setNameBangla(officeOriginUnitOrganogramList.get(i).getNameEnglish());
//                        }
//                    }
                    for (OfficeOriginUnitOrganogramDTO officeOriginUnitOrganogramDTO : officeOriginUnitOrganogramList) {
                        if (officeOriginUnitOrganogramDTO.getNameBangla() == null || officeOriginUnitOrganogramDTO.getNameBangla().isEmpty()) {
                            officeOriginUnitOrganogramDTO.setNameBangla(officeOriginUnitOrganogramDTO.getNameEnglish());
                        }
                    }
                }
                model.addAttribute("officeOriginUnitOrganogramList", officeOriginUnitOrganogramList);
            }
            model.addAttribute("service", serviceOriginDTO);
            model.addAttribute("officeOriginId", officeOriginId);
            model.addAttribute("serviceTypes", serviceTypes);
            model.addAttribute("officeOriginUnitList", officeOriginUnitList);
            return new ModelAndView("citizencharter/addCitizenCharterOrigin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewEmailTemplate.do", method = RequestMethod.GET)
    public ModelAndView getViewEmailTemplatePage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "emailTemplates",
                    "viewEmailTemplate",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "addEmailTemplate.do", method = RequestMethod.GET)
    public ModelAndView getAddEmailTemplatePage(HttpServletRequest httpServletRequest, Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("grsRoles", emailSmsSettingsService.convertToGrsRoleList());
            model.addAttribute("grievanceStatuses", emailSmsSettingsService.convertToGrievanceStatusList());
            model.addAttribute("actions", emailSmsSettingsService.convertToActionList());
            EmailTemplate emailTemplateDTO = new EmailTemplate();
            model.addAttribute("emailTemplate", emailTemplateDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "emailTemplates",
                    "addEmailTemplate",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }


    // Done ------------------------------------------------------------------------------------------------------------------


    @RequestMapping(value = "editEmailTemplate.do", method = RequestMethod.GET)
    public ModelAndView getEditEmailTemplatePage(HttpServletRequest httpServletRequest,
                                                 Authentication authentication,
                                                 Model model,
                                                 @RequestParam(value = "id") Long id) {
        if (authentication != null) {
            EmailTemplate emailTemplateDTO = this.emailService.getEmailTemplate(id);
            model.addAttribute("emailTemplate", emailTemplateDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "emailTemplates",
                    "addEmailTemplate",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewSmsTemplate.do", method = RequestMethod.GET)
    public ModelAndView getViewSmsTemplatePage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "smsTemplates",
                    "viewSmsTemplate",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "addSmsTemplate.do", method = RequestMethod.GET)
    public ModelAndView getAddSmsTemplatePage(HttpServletRequest httpServletRequest, Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("grsRoles", emailSmsSettingsService.convertToGrsRoleList());
            model.addAttribute("grievanceStatuses", emailSmsSettingsService.convertToGrievanceStatusList());
            model.addAttribute("actions", emailSmsSettingsService.convertToActionList());
            SmsTemplate smsTemplateDTO = new SmsTemplate();
            model.addAttribute("smsTemplate", smsTemplateDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "smsTemplates",
                    "addSmsTemplate",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "editSmsTemplate.do", method = RequestMethod.GET)
    public ModelAndView getEditSmsTemplatePage(HttpServletRequest httpServletRequest,
                                               Authentication authentication,
                                               Model model,
                                               @RequestParam(value = "id") Long id) {
        if (authentication != null) {
            SmsTemplate smsTemplateDTO = this.shortMessageService.getSmsTemplate(id);
            model.addAttribute("smsTemplate", smsTemplateDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "smsTemplates",
                    "addSmsTemplate",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewOccupations.do", method = RequestMethod.GET)
    public ModelAndView getViewOccupationsPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "occupation",
                    "viewOccupations",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "addOccupations.do", method = RequestMethod.GET)
    public ModelAndView getAddOccupationsPage(HttpServletRequest httpServletRequest, Authentication authentication, Model model) {
        if (authentication != null) {
            Occupation occupationDTO = new Occupation();
            model.addAttribute("occupation", occupationDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "occupation",
                    "addOccupations",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "editOccupations.do", method = RequestMethod.GET)
    public ModelAndView getEditOccupationsPage(HttpServletRequest httpServletRequest,
                                               Authentication authentication,
                                               Model model,
                                               @RequestParam(value = "id") Long id) {
        if (authentication != null) {
            Occupation occupationDTO = occupationService.getOccupation(id);
            model.addAttribute("occupation", occupationDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "occupation",
                    "addOccupations",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

     /**
          The controllers below are done by Asif Zubayer
     **/


    @RequestMapping(value = "addSpProgramme.do", method = RequestMethod.GET)
    public ModelAndView getAddSpProgramPage(HttpServletRequest httpServletRequest, Authentication authentication, Model model) {
        if (authentication != null) {
            List<String> permissions = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            if (!permissions.contains("SAVE_SP_PROGRAM")) {
                return new ModelAndView("redirect:/error-page");
            }
            SpProgram dto = new SpProgram();
            model.addAttribute("spProgramme", dto);
            model.addAttribute("showAllOffices", false);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "spProgramme",
                    "addSpProgramme",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "editSpProgram.do", method = RequestMethod.GET)
    public ModelAndView getEditSpProgramPage(HttpServletRequest httpServletRequest,
                                             Authentication authentication,
                                             Model model,
                                             @RequestParam(value = "id") Integer id) {
        if (authentication != null) {
            List<String> permissions = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            if (!permissions.contains("SAVE_SP_PROGRAM")) {
                return new ModelAndView("redirect:/error-page");
            }
            SpProgram dto = spProgrammeService.getSpProgramme(id);
            model.addAttribute("spProgramme", dto);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "spProgramme",
                    "addSpProgramme",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewEducation.do", method = RequestMethod.GET)
    public ModelAndView getViewEducationPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "education",
                    "viewEducation",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "addEducation.do", method = RequestMethod.GET)
    public ModelAndView getAddEducationPage(HttpServletRequest httpServletRequest, Authentication authentication, Model model) {
        if (authentication != null) {
            Education educationDTO = new Education();
            model.addAttribute("education", educationDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "education",
                    "addEducation",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "editEducation.do", method = RequestMethod.GET)
    public ModelAndView getEditEducationPage(HttpServletRequest httpServletRequest,
                                             Authentication authentication,
                                             Model model,
                                             @RequestParam(value = "id") Long id) {
        if (authentication != null) {
            Education educationDTO = educationService.getEducation(id);
            model.addAttribute("education", educationDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "education",
                    "addEducation",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewServices.do", method = RequestMethod.GET)
    public ModelAndView getViewServicesPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "service",
                    "viewServices",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "addServices.do", method = RequestMethod.GET)
    public ModelAndView getAddServicePage(HttpServletRequest httpServletRequest, Authentication authentication, Model model) {
        if (authentication != null) {
            ServiceOrigin serviceOriginDTO = new ServiceOrigin();
            model.addAttribute("service", serviceOriginDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "service",
                    "addService",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "editServices.do", method = RequestMethod.GET)
    public ModelAndView getEditServicePage(HttpServletRequest httpServletRequest,
                                           Authentication authentication,
                                           Model model,
                                           @RequestParam(value = "id") Long id) {
        if (authentication != null) {
            ServiceOrigin serviceOriginDTO = officeService.getServiceOrigin(id);
            model.addAttribute("service", serviceOriginDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "service",
                    "addService",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "setupEmailSMS.do", method = RequestMethod.GET)
    public ModelAndView getSetupEmailSMSPage(Authentication authentication, Model model, HttpServletRequest request, @RequestParam(value = "id") Long id) {
        if (authentication != null) {
            EmailSmsSettings emailSmsSettingsDTO = emailSmsSettingsService.getEmailSmsSettings(id);
            model.addAttribute("settings", emailSmsSettingsDTO);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "emailSmsSettings",
                    "setupEmailSMS",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "manageOffice.do", method = RequestMethod.GET)
    public ModelAndView getManageOfficePage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String viewName = "";
            OfficeSetup officeSetupDTO = new OfficeSetup();
            model.addAttribute("office", officeSetupDTO);
            if (userInformation.getOisfUserType() != null && userInformation.getIsOfficeAdmin()) {
                viewName = "admin";
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                Boolean isMinistryOrDivisionLevelOffice = officeService.isMinistryOrDivisionLevelOffice(officeInformation.getOfficeId());
                model.addAttribute("manageOwnOffice", true);
                model.addAttribute("officeId", officeInformation.getOfficeId());
                model.addAttribute("officeName", officeInformation.getOfficeNameBangla());
                model.addAttribute("officeOriginId", officeInformation.getOfficeOriginId());
                model.addAttribute("officeUnitOrganogramId", officeInformation.getOfficeUnitOrganogramId());
                model.addAttribute("canNotChangeAO", !isMinistryOrDivisionLevelOffice);
            } else if (userInformation.getGrsUserType() != null && userInformation.getGrsUserType().equals(GRSUserType.SUPER_ADMIN)) {
                viewName = "superAdmin";
            } else {
                return new ModelAndView("redirect:/error-page");
            }
            model.addAttribute("searchableOffices", officeService.getOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "officeManagement",
                    viewName);
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "manageSafetyNet.do", method = RequestMethod.GET)
    public ModelAndView getManageSafetyNetPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String viewName = "";
            OfficeSetup officeSetupDTO = new OfficeSetup();
            model.addAttribute("office", officeSetupDTO);
            if (userInformation.getOisfUserType() != null) {

                Set<String> permissions = this.permissionsToUsersDAO.findByOisfUserId(userInformation.getUserId())
                        .stream()
                        .map(permissionsToUsers -> permissionsToUsers.getPermission().getName())
                        .collect(Collectors.toSet());

                if (!permissions.contains("VIEW_MISSING_OFFICER_TYPE")) {
                    return new ModelAndView("redirect:/error-page");
                }

                viewName = "admin";
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                Boolean isMinistryOrDivisionLevelOffice = officeService.isMinistryOrDivisionLevelOffice(officeInformation.getOfficeId());
                model.addAttribute("officeId", officeInformation.getOfficeId());
                model.addAttribute("officeName", officeInformation.getOfficeNameBangla());
                model.addAttribute("officeOriginId", officeInformation.getOfficeOriginId());
                model.addAttribute("officeUnitOrganogramId", officeInformation.getOfficeUnitOrganogramId());
                model.addAttribute("canNotChangeAO", !isMinistryOrDivisionLevelOffice);
            } else if (userInformation.getGrsUserType() != null && userInformation.getGrsUserType().equals(GRSUserType.SUPER_ADMIN)) {
                viewName = "superAdmin";
            } else {
                return new ModelAndView("redirect:/error-page");
            }
            model.addAttribute("searchableOffices", officeService.getOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "safetyNetManagement",
                    viewName);
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "manageOfficeMissing.do", method = RequestMethod.GET)
    public ModelAndView getManageOfficeMissingPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String viewName = "";
            OfficeSetup officeSetupDTO = new OfficeSetup();
            model.addAttribute("office", officeSetupDTO);
            if (userInformation.getOisfUserType() != null) {

                Set<String> permissions = this.permissionsToUsersDAO.findByOisfUserId(userInformation.getUserId())
                        .stream()
                        .map(permissionsToUsers -> permissionsToUsers.getPermission().getName())
                        .collect(Collectors.toSet());

                if (!permissions.contains("VIEW_MISSING_OFFICER_TYPE")) {
                    return new ModelAndView("redirect:/error-page");
                }

                viewName = "admin";
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                Boolean isMinistryOrDivisionLevelOffice = officeService.isMinistryOrDivisionLevelOffice(officeInformation.getOfficeId());
                model.addAttribute("officeId", officeInformation.getOfficeId());
                model.addAttribute("officeName", officeInformation.getOfficeNameBangla());
                model.addAttribute("officeOriginId", officeInformation.getOfficeOriginId());
                model.addAttribute("officeUnitOrganogramId", officeInformation.getOfficeUnitOrganogramId());
                model.addAttribute("canNotChangeAO", !isMinistryOrDivisionLevelOffice);
            } else if (userInformation.getGrsUserType() != null && userInformation.getGrsUserType().equals(GRSUserType.SUPER_ADMIN)) {
                viewName = "superAdmin";
            } else {
                return new ModelAndView("redirect:/error-page");
            }
            model.addAttribute("searchableOffices", officeService.getOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "officeManagementMissing",
                    viewName);
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "layerWiseCustomReport.do", method = RequestMethod.GET)
    public ModelAndView getLayerwiseCustomReportPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String viewName = "";
            OfficeSetup officeSetupDTO = new OfficeSetup();
            model.addAttribute("office", officeSetupDTO);
            if (userInformation.getOisfUserType() != null) {

                Set<String> permissions = this.permissionsToUsersDAO.findByOisfUserId(userInformation.getUserId())
                        .stream()
                        .map(permissionsToUsers -> permissionsToUsers.getPermission().getName())
                        .collect(Collectors.toSet());

                if (!permissions.contains("VIEW_LAYERWISE_CUSTOM_REPORT")) {
                    return new ModelAndView("redirect:/error-page");
                }

                viewName = "admin";
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                Boolean isMinistryOrDivisionLevelOffice = officeService.isMinistryOrDivisionLevelOffice(officeInformation.getOfficeId());
//                model.addAttribute("manageOwnOffice", true);
                model.addAttribute("officeId", officeInformation.getOfficeId());
                model.addAttribute("officeName", officeInformation.getOfficeNameBangla());
                model.addAttribute("officeOriginId", officeInformation.getOfficeOriginId());
                model.addAttribute("officeUnitOrganogramId", officeInformation.getOfficeUnitOrganogramId());
                model.addAttribute("canNotChangeAO", !isMinistryOrDivisionLevelOffice);
            } else if (userInformation.getGrsUserType() != null && userInformation.getGrsUserType().equals(GRSUserType.SUPER_ADMIN)) {
                viewName = "superAdmin";
            } else {
                return new ModelAndView("redirect:/error-page");
            }
            model.addAttribute("searchableOffices", officeService.getOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "layerWiseReport",
                    viewName);
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "timeWiseComplainantReport.do", method = RequestMethod.GET)
    public ModelAndView getTimeWiseComplainantReportPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String viewName = "";
            OfficeSetup officeSetupDTO = new OfficeSetup();
            model.addAttribute("office", officeSetupDTO);
            if (userInformation.getOisfUserType() != null) {

                Set<String> permissions = this.permissionsToUsersDAO.findByOisfUserId(userInformation.getUserId())
                        .stream()
                        .map(permissionsToUsers -> permissionsToUsers.getPermission().getName())
                        .collect(Collectors.toSet());

                if (!permissions.contains("VIEW_TIMEWISE_COMPLAINANT_REPORT")) {
                    return new ModelAndView("redirect:/error-page");
                }

                viewName = "admin";
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                Boolean isMinistryOrDivisionLevelOffice = officeService.isMinistryOrDivisionLevelOffice(officeInformation.getOfficeId());
//                model.addAttribute("manageOwnOffice", true);
                model.addAttribute("officeId", officeInformation.getOfficeId());
                model.addAttribute("officeName", officeInformation.getOfficeNameBangla());
                model.addAttribute("officeOriginId", officeInformation.getOfficeOriginId());
                model.addAttribute("officeUnitOrganogramId", officeInformation.getOfficeUnitOrganogramId());
                model.addAttribute("canNotChangeAO", !isMinistryOrDivisionLevelOffice);
            } else if (userInformation.getGrsUserType() != null && userInformation.getGrsUserType().equals(GRSUserType.SUPER_ADMIN)) {
                viewName = "superAdmin";
            } else {
                return new ModelAndView("redirect:/error-page");
            }
            model.addAttribute("searchableOffices", officeService.getOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "timeWiseComplainantReport",
                    viewName);
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "manageSubOffices.do", method = RequestMethod.GET)
    public ModelAndView getManageSubOfficesPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            OfficeSetup officeSetupDTO = new OfficeSetup();
            model.addAttribute("office", officeSetupDTO);
            if (userInformation.getOisfUserType() != null && userInformation.getIsOfficeAdmin()) {
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                model.addAttribute("showAllOffices", true);
                model.addAttribute("showChildOfficesOnly", false);
                model.addAttribute("officeId", officeInformation.getOfficeId());
                model.addAttribute("officeOriginId", officeInformation.getOfficeOriginId());
                model.addAttribute("officeUnitOrganogramId", officeInformation.getOfficeUnitOrganogramId());
                model.addAttribute("searchableOffices", officeService.getDescendantOfficeSearchingData());
            } else {
                return new ModelAndView("redirect:/error-page");
            }
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "officeManagement",
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    /**
            The controllers below are done by Nayeem Khan
    **/

    @RequestMapping(value = "manageCitizenCharter.do", method = RequestMethod.GET)
    public ModelAndView getManageCitizenCharterPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String viewName = "";
            CitizensCharterOrigin citizensCharterOriginDTO = new CitizensCharterOrigin();
            model.addAttribute("office", citizensCharterOriginDTO);
            model.addAttribute("officeOriginInfoId", citizensCharterOriginDTO.getId());
            if (userInformation.getUserType().equals(UserType.OISF_USER)) {
                if (!userInformation.getIsOfficeAdmin()) {
                    return new ModelAndView("redirect:/error-page");
                }
                model.addAttribute("showChildOfficesOnly", false);
                model.addAttribute("showAllOffices", true);
                viewName = "admin";
            } else {
                viewName = "superAdmin";
            }
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "citizenCharterSetup",
                    viewName);
        }
        return new ModelAndView("redirect:/error-page");
    }


    @RequestMapping(value = "spProgrammeSetup.do", method = RequestMethod.GET)
    public ModelAndView getspProgramSetupPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String viewName = "";
            if (userInformation.getUserType().equals(UserType.OISF_USER)) {
                if (!userInformation.getIsOfficeAdmin()) {
                    return new ModelAndView("redirect:/error-page");
                }
                viewName = "admin";
            } else {
                viewName = "superAdmin";
            }

            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "spProgramme",
                    "viewSpProgrammeSetup",
                    viewName
            );
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewOffice.do", method = RequestMethod.GET)
    public ModelAndView getGRSOfficePage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            model.addAttribute("showAllOffices", false);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "viewGRSOffice",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewAllOffice.do", method = RequestMethod.GET)
    public ModelAndView getAllGRSOfficePage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            List<BaseObjectDTO> offices = this.officesGroDAO.findAll().stream().map((OfficesGRO officesGRO) -> {
                String name = officesGRO.getOfficeId() == 0 ? "অভিযোগ ব্যবস্থাপনা সেল" : officesGRO.getOfficeNameBangla();
                return BaseObjectDTO.builder()
                        .id(officesGRO.getId())
                        .name(name)
                        .build();
            }).collect(Collectors.toList());
            model.addAttribute("grsOffices", offices);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "viewAllGRSOffice",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "manageCell.do", method = RequestMethod.GET)
    public ModelAndView getGRSCellPage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "cellManagement",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "manageCentralDashboardReceivers.do", method = RequestMethod.GET)
    public ModelAndView getCentralDashboardReceiverSettings(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "settings",
                    "centralDashboardReceiverSettings",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "/provideNudge.do", method = RequestMethod.GET)
    public ModelAndView getTimeExpiredGrievancesPage(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            Long officeId = userInformation.getOfficeInformation().getOfficeId();
            String type = request.getParameter("type");
            String requestParams = request.getParameter("params");
            if (StringUtil.isValidString(requestParams)) {
                Long officeIdParam = StringUtil.decodeOfficeIdOnDashboardDrillDown(requestParams);
                Office office = officeService.findOne(officeIdParam);
                if (office != null) {
                    officeId = officeIdParam;
                }
            }
            model.addAttribute("isAppeal", type != null && type.equals("appeal"));
            model.addAttribute("officeId", officeId);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "nudge",
                    "viewTimeExpiredGrievances",
                    "admin"
            );
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "uploadCitizensCharter.do", method = RequestMethod.GET)
    public ModelAndView uploadCitizensCharter(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            String viewName = "";
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if (userInformation.getUserType().equals(UserType.OISF_USER)) {
                if (!userInformation.getIsOfficeAdmin()) {
                    return new ModelAndView("redirect:/error-page");
                }
                model.addAttribute("showAllOffices", true);
                model.addAttribute("showChildOfficesOnly", false);
                viewName = "admin";
            } else {
                viewName = "superAdmin";
            }
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "citizencharter",
                    "uploadCitizensCharter",
                    viewName);
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "fileUploadSetup.do", method = RequestMethod.GET)
    public ModelAndView getFileUploadSetupForm(HttpServletRequest request, Authentication authentication, Model model) throws IllegalAccessException {
        if (authentication != null) {
            Integer maxFileSize = generalSettingsService.getMaximumFileSize();
            String fileTypes = generalSettingsService.getAllowedFileTypes();
            List<String> allowedFileTypes = Arrays.asList(fileTypes.split("\\|"));
            model.addAttribute("maxFileSize", maxFileSize);
            model.addAttribute("allowedFileTypes", allowedFileTypes);
            model.addAttribute("fileSizeLabel", generalSettingsService.getAllowedFileSizeLabel());
            model.addAttribute("fileTypesLabel", generalSettingsService.getAllowedFileTypesLabel());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "settings",
                    "fileSettings",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "systemNotificationSettings.do", method = RequestMethod.GET)
    public ModelAndView getSystemNotificationSettingsForm(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null) {
            String email = generalSettingsService.getSettingsValueByFieldName(Constant.SYSTEM_NOTIFICATION_EMAIL);
            String phoneNumber = generalSettingsService.getSettingsValueByFieldName(Constant.SYSTEM_NOTIFICATION_PHONE_NUMBER);
            model.addAttribute("email", email);
            model.addAttribute("phoneNumber", phoneNumber);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "settings",
                    "systemNotificationSettings",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "viewRatingsAndFeedback.do", method = RequestMethod.GET)
    public ModelAndView viewAllRatingsAndFeedback(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            Long officeId = userInformation.getOfficeInformation().getOfficeId();
            String type = request.getParameter("type");
            String requestParams = request.getParameter("params");
            if (StringUtil.isValidString(requestParams)) {
                Long officeIdParam = StringUtil.decodeOfficeIdOnDashboardDrillDown(requestParams);
                Office office = officeService.findOne(officeIdParam);
                if (office != null) {
                    officeId = officeIdParam;
                }
            }
            model.addAttribute("type", type);
            model.addAttribute("officeId", officeId);
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "grievances",
                    "viewRatingsAndFeedback",
                    "admin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "dashboard.do", method = RequestMethod.GET)
    public ModelAndView getGeneralDashboard(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            if (Utility.canViewDashboard(userInformation)) {
                Boolean isMobileLogin = Utility.isLoggedInFromMobile(userInformation);
                if (isMobileLogin) {
                    return new ModelAndView("redirect:/dashboardMobile.do");
                }
                String requestParams = request.getParameter("params");
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                Boolean isCentralDashboardUser = userInformation.getIsCentralDashboardUser();
                Long layerLevel = officeInformation.getLayerLevel();
                Long officeId = officeInformation.getOfficeId();
                Office userOffice = this.officeService.getOffice(officeId);
                Integer divisionId = userOffice.getDivisionId();
                Integer districtId = userOffice.getDistrictId();
                Integer upazilaId = userOffice.getUpazilaId();
                Long officeLevel = officeInformation.getLayerLevel();
                Boolean isMinistryOrDivisionLevelOffice = officeService.isMinistryOrDivisionLevelOffice(officeId);
                String officeName = messageService.isCurrentLanguageInEnglish() ? officeInformation.getOfficeNameEnglish() : officeInformation.getOfficeNameBangla();
                Boolean isDrilledDown = false;
                if (StringUtil.isValidString(requestParams)) {
                    String decodedParams = StringUtils.newStringUtf8(Base64.decodeBase64(requestParams.substring(20)));
                    Long targetOfficeId = Long.parseLong(decodedParams);
                    Office targetOffice = officeService.findOne(targetOfficeId);
                    List<Long> parentOfficeIds = officeService.getAncestorOfficeIds(targetOfficeId);
                    if (targetOffice != null && ((parentOfficeIds.contains(officeId) || userInformation.getIsCentralDashboardUser()) || officeService.canViewDashboardAsFieldCoordinator(targetOfficeId))) {
                        officeId = targetOfficeId;
                        officeName = messageService.isCurrentLanguageInEnglish() ? targetOffice.getNameEnglish() : targetOffice.getNameBangla();
                        isDrilledDown = true;
                    } else {
                        return new ModelAndView("redirect:/error-page");
                    }
                }
                List<OfficeSearchDTO> officeSearchDTOList = officeService.getGrsEnabledOfficeSearchingData();
                Boolean isMinistryLevelGro = false;
                Boolean isCabinetDivisionUser = false;
                if (isMinistryOrDivisionLevelOffice && Utility.isUserAnGROUser(userInformation)) {
                    isMinistryLevelGro = true;
                    if(officeId != 28L) {
                        officeSearchDTOList = officeService.getDescendantOfficeSearchingData();
                    } else {
                        officeSearchDTOList = officeService.getTopLayerOffices();
                        isCabinetDivisionUser = true;
                    }
                    model.addAttribute("showAllOffices", true);
                    model.addAttribute("showChildOfficesOnly", true);
                }
                model.addAttribute("isCentralDashboardUser", isCentralDashboardUser);
                model.addAttribute("layerLevel", layerLevel);
                model.addAttribute("officeId", officeId);
                model.addAttribute("officeName", officeName);
                model.addAttribute("currentMonthYear", messageService.getCurrentMonthYearAsString());
                model.addAttribute("currentYear", messageService.getCurrentYearString());
                model.addAttribute("currentDateMonthYear", messageService.getCurrentDateMonthYearAsString());
                model.addAttribute("isDrilledDown", isDrilledDown);
                model.addAttribute("canViewGrievanceDashboard", Utility.isUserAnGROUser(userInformation) || Utility.isUserAHOOUser(userInformation) || Utility.isUserACentralDashboardRecipient(userInformation));
                model.addAttribute("canViewAppealAndSubOfficeDashboard", officeService.hasAccessToAoAndSubOfficesDashboard(officeId));
                model.addAttribute("isDivisionLevelFC", Utility.isDivisionLevelFC(authentication));
                model.addAttribute("isDistrictLevelFC", Utility.isDistrictLevelFC(authentication));
                model.addAttribute("isMinistryLevelGro", isMinistryLevelGro);
                model.addAttribute("searchableOffices", officeSearchDTOList);
                model.addAttribute("officeLevel", officeLevel);
                model.addAttribute("divisionId", divisionId);
                model.addAttribute("districtId", districtId);
                model.addAttribute("upazilaId", upazilaId);
                model.addAttribute("isCabinetDivisionUser",isCabinetDivisionUser);
                return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                        authentication,
                        request,
                        "dashboard2",
                        "hooDashboard",
                        "dashboard");
            }
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "grsApplicationPrivacyPolicy.do", method = RequestMethod.GET)
    public ModelAndView getGrsApplicationPrivacyPolicy(Authentication authentication, Model model, HttpServletRequest request) {
        Map attrs = new WeakHashMap() {{
            put("isProductionMode", true);
            put("grsUser", true);
            put("superAdmin", true);
        }};
        model.addAllAttributes(attrs);
        return new ModelAndView("grsPrivacyPolicy");
    }

    @RequestMapping(value = "grsUsers.do", method = RequestMethod.GET)
    public ModelAndView getUsersCount(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            model.addAttribute("userCount", BanglaConverter.convertToBanglaDigit(complainantService.countAll()));
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "grsUser",
                    "viewGrsUserCount",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "addGrsUsers.do", method = RequestMethod.GET)
    public ModelAndView getAddGrsUserPage(HttpServletRequest httpServletRequest, Authentication authentication, Model model) {
        if (authentication != null) {
            SuperAdmin superAdmin = new SuperAdmin();
            GrsUser superAdminDTO = grsUserService.convertToSuperAdminDTO(superAdmin);
            model.addAttribute("grsUserFormData", superAdminDTO);
            model.addAttribute("showAllOffices", false);
            model.addAttribute("searchableOffices", officeService.getGrsEnabledOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    httpServletRequest,
                    "grsUser",
                    "addGrsUser",
                    "superAdmin");
        }
        return new ModelAndView("redirect:/error-page");
    }

    @RequestMapping(value = "manageGrievance.do", method = RequestMethod.GET)
    public ModelAndView getManageGrievancePage(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) {
            UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
            String viewName = "";
            OfficeSetup officeSetupDTO = new OfficeSetup();
            model.addAttribute("office", officeSetupDTO);
            if (userInformation.getOisfUserType() != null && userInformation.getIsOfficeAdmin()) {
                viewName = "admin";
                OfficeInformation officeInformation = userInformation.getOfficeInformation();
                Boolean isMinistryOrDivisionLevelOffice = officeService.isMinistryOrDivisionLevelOffice(officeInformation.getOfficeId());
                model.addAttribute("manageOwnOffice", true);
                model.addAttribute("officeId", officeInformation.getOfficeId());
                model.addAttribute("officeName", officeInformation.getOfficeNameBangla());
                model.addAttribute("officeOriginId", officeInformation.getOfficeOriginId());
                model.addAttribute("officeUnitOrganogramId", officeInformation.getOfficeUnitOrganogramId());
                model.addAttribute("canNotChangeAO", !isMinistryOrDivisionLevelOffice);
            } else if (userInformation.getGrsUserType() != null && userInformation.getGrsUserType().equals(GRSUserType.SUPER_ADMIN)) {
                viewName = "superAdmin";
            } else {
                return new ModelAndView("redirect:/error-page");
            }
            model.addAttribute("searchableOffices", officeService.getOfficeSearchingData());
            return modelViewService.addNecessaryAttributesAndReturnViewPage(model,
                    authentication,
                    request,
                    "management",
                    "grievanceManagement",
                    viewName);
        }
        return new ModelAndView("redirect:/error-page");
    }

}
