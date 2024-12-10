package com.grs.api.controller;


import com.grs.api.config.security.OISFUserDetailsServiceImpl;
import com.grs.api.model.UserInformation;
import com.grs.core.domain.doptor.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oisfuser")
@RequiredArgsConstructor
public class OISFUserDetailsController {

    private final OISFUserDetailsServiceImpl oisfUserDetailsService;

    @PostMapping("/userinfo")
    public UserInformation getUserInfo(@RequestBody UserInfo userInfo){
        if (userInfo == null){
            return null;
        }
        return oisfUserDetailsService.getUserInfo(userInfo);
    }
}
