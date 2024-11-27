package com.grs.grs_client.controller;

import com.grs.grs_client.common.IDP_Client;
import com.grs.grs_client.common.SSOPropertyReader;
import com.grs.grs_client.domain.RedirectMap;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.model.SubMenuDTO;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.service.ModelAndViewService;
import com.grs.grs_client.utils.Constant;
import com.grs.grs_client.utils.CookieUtil;
import com.grs.grs_client.utils.StringUtil;
import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
public class ViewPageController {

    @Autowired
    private ModelAndViewService modelViewService;

    @Value("${app.base.url:''}")
    private String appBaseUrl;
    @Value("${idp.url:''}")
    private String idpUrl;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView firstPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        return modelViewService.returnViewsForNormalPages(model, request, "index");
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
            boolean isOthersComplainant = Utility.isUserAnOthersComplainant(authentication);
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
            boolean isOthersComplainant = Utility.isUserAnOthersComplainant(authentication);
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

}
