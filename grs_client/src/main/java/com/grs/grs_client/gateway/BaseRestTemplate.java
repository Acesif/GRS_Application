package com.grs.grs_client.gateway;


import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
@PropertySources({
        @PropertySource("classpath:application.yml")})
public class BaseRestTemplate {

    @Autowired
    private Environment env;

    @Autowired
    protected RestTemplate restTemplate;
    private static String PROTOCOL;
    public static String HOST;
    public static String PORT;
    private static String SERVICE_URL = "";
    private String token;

    @PostConstruct
    void init() {

        PROTOCOL = env.getProperty("api.service.protocol");
        HOST = env.getProperty("api.service.host");
        PORT = env.getProperty("api.service.port");

        if (PORT != null && PORT.length() > 0) {
            SERVICE_URL = PROTOCOL + "://" + HOST + ":" + PORT;
        } else {
            SERVICE_URL = PROTOCOL + "://" + HOST;
        }

    }
    public static String getUrl() {
        return SERVICE_URL;
    }
    protected String getToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return userInformation.getToken();
    }
}
