package com.grs.grs_client.service;

import com.grs.api.model.NotificationsDTO;
import com.grs.core.service.NotificationService;
import com.grs.grs_client.enums.OISFUserType;
import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.model.SubMenuDTO;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.CookieUtil;
import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ModelAndViewService {

//    @Autowired
//    private NotificationService notificationService;
    public ModelAndView returnViewsForNormalPages(
                                                  Model model,
                                                  HttpServletRequest request,
                                                  String viewName) {
        SubMenuDTO subMenuDTO;
        boolean isLoggedIn, isGrsUser, isOisfUser, isMobileLogin, isOthersComplainant, isMyGovLogin;
        String token = "";
            isLoggedIn = false;
            isGrsUser = false;
            isOthersComplainant = false;
            isOisfUser = false;
            isMobileLogin = false;
            isMyGovLogin = false;
            subMenuDTO = SubMenuDTO.builder()
                    .nameEnglish("Log In")
                    .nameBangla("লগইন")
                    .link("/login?a=0")
                    .build();

        String languageCode = "en";
        model.addAttribute("lang", languageCode);
        model.addAttribute("menu", subMenuDTO);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("token", token);
        model.addAttribute("isGrsUser", isGrsUser);
        model.addAttribute("isOthersComplainant", isOthersComplainant);
        model.addAttribute("isOisfUser", isOisfUser);
        model.addAttribute("isMyGovLogin", isMyGovLogin);
        model.addAttribute("isMobileLogin", isMobileLogin);
        model.addAttribute("isProductionMode", false);
        return new ModelAndView(viewName);
    }
    public ModelAndView addNecessaryAttributesAndReturnViewPage(Model model,
                                                                Authentication authentication,
                                                                HttpServletRequest request,
                                                                String mainPageFragmentName,
                                                                String mainPageFragmentValue,
                                                                String viewName) {

        List<String> permissions = authentication.getAuthorities()
                .stream()
                .map(grantedAuthorityImpl -> grantedAuthorityImpl.getAuthority())
                .collect(Collectors.toList());

        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        String username = userInformation.getUsername();
        if (userInformation.getUserType().equals(UserType.OISF_USER) &&
                !userInformation.getOisfUserType().equals(OISFUserType.SUPER_ADMIN)) {
            username = userInformation.getOfficeInformation().getName();
        }
        String languageCode = CookieUtil.getValue(request, "lang");

        model.addAttribute("name", username);
        model.addAttribute("fragmentPath", mainPageFragmentName);
        model.addAttribute("fragmentName", mainPageFragmentValue);
        model.addAttribute("lang", languageCode);
        model.addAttribute("permissions", permissions);
//        model.addAttribute("isProductionMode", Boolean.valueOf(environment.getProperty("environment.production")));
        model.addAttribute("isProductionMode", false);

        Boolean isGrsUser = Utility.isUserAnGRSUser(authentication);
        Boolean isOthersComplainant = Utility.isUserAnOthersComplainant(authentication);
//        if(!isGrsUser && !userInformation.getUserType().equals(UserType.SYSTEM_USER)){
//            NotificationsDTO notifications = this.notificationService.findByOfficeIdAndEmployeeRecordIdAndOfficeUnitOrganogramIdOrderByIdDesc(
//                    userInformation.getOfficeInformation().getOfficeId(), userInformation.getOfficeInformation().getEmployeeRecordId(), userInformation.getOfficeInformation().getOfficeUnitOrganogramId()
//            );
//            model.addAttribute("notificationList", notifications);
//        }
        // todo: need to add Notification Service #blocker
        Boolean isMyGovLogin = userInformation.getIsMyGovLogin() != null && userInformation.getIsMyGovLogin();
        Boolean isMobileLogin = Utility.isLoggedInFromMobile(authentication);
        Boolean isCellGro = userInformation.getIsCellGRO() == null ? false : userInformation.getIsCellGRO();
        Boolean showDrawerMenu = isMobileLogin && !isGrsUser;
        model.addAttribute("grsUser", isGrsUser);
        model.addAttribute("isOthersComplainant", isOthersComplainant);
        model.addAttribute("isGro", Utility.isUserAnGROUser(authentication));
        model.addAttribute("isAppealOfficer", (userInformation.getIsAppealOfficer() || isCellGro));
        model.addAttribute("isOfficeAdmin", userInformation.getIsOfficeAdmin());
        model.addAttribute("isHoo", Utility.isUserAHOOUser(authentication));
        model.addAttribute("isFieldCoordinator", Utility.isFieldCoordinator(authentication));
        model.addAttribute("isCentralDashboardRecipient", Utility.isUserACentralDashboardRecipient(authentication));
        model.addAttribute("isMobileLogin", isMobileLogin);
        model.addAttribute("showDrawerMenu", showDrawerMenu);
        Boolean isMinistryLevel = false;
        Boolean isAdmin = false;

        if(!isGrsUser) {
            isMinistryLevel = userInformation.getIsOfficeAdmin() && userInformation.getOfficeInformation().getLayerLevel().equals(1L);
            isAdmin = userInformation.getIsOfficeAdmin();
        }

        model.addAttribute("isMinistryLevelAdmin",  isMinistryLevel);
        model.addAttribute("isAdmin",  isAdmin);

        if (!model.containsAttribute("superAdmin")){
            model.addAttribute("superAdmin",Utility.isUserASuperAdmin(authentication));
        }
        if(!model.containsAttribute("showAllOffices")) {
            model.addAttribute("showAllOffices",Utility.isUserASuperAdmin(authentication));
        }
        if(!model.containsAttribute("showChildOfficesOnly")) {
            model.addAttribute("showChildOfficesOnly",false);
        }
        Boolean isCellMember = false;
        if (userInformation.getUserType().equals(UserType.OISF_USER) && !userInformation.getOisfUserType().equals(OISFUserType.SUPER_ADMIN)
                && userInformation.getOfficeInformation().getOfficeId() == 0 ){
            isCellMember = true;
        }
        model.addAttribute("isMyGovLogin", isMyGovLogin);
        model.addAttribute("isCellMember", isCellMember);
        model.addAttribute("isCellGRO", Utility.isCellGRO(authentication));
        model.addAttribute("canViewDashboard", Utility.canViewDashboard(authentication));
        model.addAttribute("token", userInformation.getToken());

        viewName = showDrawerMenu ? "admin_mobile" : viewName;
        return new ModelAndView(viewName);
    }
}
