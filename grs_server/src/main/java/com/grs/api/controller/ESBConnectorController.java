package com.grs.api.controller;

import com.grs.core.service.ESBConnectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.WeakHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ESBConnector")
public class  ESBConnectorController {

    private final ESBConnectorService esbConnectorService;

    @RequestMapping(value = "/baseUrlWithPort",method= RequestMethod.GET)
    public String getBaseUrlWithPort(){
        return this.esbConnectorService.getBaseUrlWithPort();
    }


    @RequestMapping(value = "/authenticationToken",method=RequestMethod.GET)
    public Object getAuthenticationToken(){
        return this.esbConnectorService.getAuthenticationToken();
    }

    @RequestMapping(value = "/loginAuthorization",method = RequestMethod.POST)
    public WeakHashMap<String, String> getLoginAuthorization(
           @RequestParam  String authToken,
           @RequestParam String userName,
           @RequestParam String password,
           @RequestParam String nonce){
        return this.esbConnectorService.getLoginAuthorization(authToken,userName,password,nonce);

    }
}
