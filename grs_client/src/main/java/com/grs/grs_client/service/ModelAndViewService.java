package com.grs.grs_client.service;

import com.grs.grs_client.model.SubMenuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class ModelAndViewService {
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
}
