package com.grs.api.controller;


import com.grs.api.model.UserInformation;
import com.grs.core.domain.grs.LoginTrace;
import com.grs.core.service.LoginTraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logninTrace")
public class LoginTraceController {

    private final LoginTraceService loginTraceService;


    @RequestMapping(value = "/getLoginTrace",method = RequestMethod.POST)
    public LoginTrace save(@RequestBody LoginTrace loginTrace){
        return loginTraceService.save(loginTrace);

    }
    @RequestMapping(value = "/saveMyGovLogin",method = RequestMethod.POST)
    public LoginTrace saveMyGovLogin(@RequestBody UserInformation userInformation){
        return loginTraceService.saveSSOLogin(userInformation);
    }
    @RequestMapping(value = "/saveSSOLogin",method = RequestMethod.POST)
    public LoginTrace saveSSOLogin(@RequestBody UserInformation userInformation){
        return loginTraceService.saveSSOLogin(userInformation);
    }

    @RequestMapping(value = "/saveGRSLogin",method = RequestMethod.POST)
    public LoginTrace saveGRSLogin(@RequestBody UserInformation userInformation){
        return loginTraceService.saveSSOLogin(userInformation);
    }

}
