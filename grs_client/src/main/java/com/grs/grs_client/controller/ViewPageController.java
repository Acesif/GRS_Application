package com.grs.grs_client.controller;
import com.grs.grs_client.common.IDP_Client;
import com.grs.grs_client.common.SSOPropertyReader;
import com.grs.grs_client.config.GrantedAuthorityImpl;
import com.grs.grs_client.config.TokenAuthenticationServiceUtil;
import com.grs.grs_client.config.UserDetailsImpl;
import com.grs.grs_client.domain.RedirectMap;
import com.grs.grs_client.enums.GRSUserType;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.gateway.AuthGateway;
import com.grs.grs_client.gateway.ComplainantGateway;
import com.grs.grs_client.gateway.MessageGateway;
import com.grs.grs_client.gateway.OfficesGateway;
import com.grs.grs_client.model.*;
import com.grs.grs_client.service.ModelAndViewService;
import com.grs.grs_client.utils.Constant;
import com.grs.grs_client.utils.CookieUtil;
import com.grs.grs_client.utils.StringUtil;
import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ViewPageController {

    @Autowired
    private ModelAndViewService modelViewService;
    @Autowired
    private OfficesGateway officeService;
    @Autowired
    private AuthGateway authGateway;
    @Autowired
    private MessageGateway messageService;
    @Autowired
    private ComplainantGateway complainantService;


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
                e.printStackTrace();
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
        System.out.println(languageCode);
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
                Boolean isDrilledDown = false;
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

}
