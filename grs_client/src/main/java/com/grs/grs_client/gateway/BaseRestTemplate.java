package com.grs.grs_client.gateway;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
@PropertySources({
        @PropertySource("classpath:application.properties")})
public class BaseRestTemplate {

    @Autowired
    private Environment env;

    @Autowired
    protected RestTemplate restTemplate;


    private static String PROTOCOL;


    public static String HOST;


    public static String PORT;

    public static String PATH = "api";

    private static String SERVICE_URL = "";

    private static String FIELD_ATTACHMENT_URL = "";

    private String token;


    @PostConstruct
    void init() {

        PROTOCOL = env.getProperty("api.service.protocol");
        HOST = env.getProperty("api.service.host");
        PORT = env.getProperty("api.service.port");
        PATH = env.getProperty("api.service.context");
        FIELD_ATTACHMENT_URL = env.getProperty("fv.attachment.url");

        if (PORT != null && PORT.length() > 0) {
            SERVICE_URL = PROTOCOL + "://" + HOST + ":" + PORT + "/" + PATH + "/";
        } else {
            SERVICE_URL = PROTOCOL + "://" + HOST + "/" + PATH + "/";
        }

    }

    public static String getUrl() {
        return SERVICE_URL;
    }

    protected String getToken(){
        return "";
    }

    public static String getFieldAttachmentUrl() {
        return FIELD_ATTACHMENT_URL;
    }


}
