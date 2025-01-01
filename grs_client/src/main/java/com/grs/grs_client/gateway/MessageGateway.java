package com.grs.grs_client.gateway;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class MessageGateway extends BaseRestTemplate {


    String GRS_CORE_CONTEXT_PATH = "/grs_server";



    public boolean isCurrentLanguageInEnglish() {


        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/message/isCurrentLanguageInEnglish";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);


        HttpEntity<?> entity = new HttpEntity<>(headers);


        ResponseEntity<Boolean> response = null;
        try {

            response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<Boolean>() {

                    }
            );

        } catch (HttpClientErrorException e) {
            log.error("HTTP client Error: ", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error ", e.getMessage());
        }


        return response.getBody();
    }

    public String getCurrentMonthYearAsString() {

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/message/getCurrentMonthYearAsString";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);


        HttpEntity<?> entity = new HttpEntity<>(headers);


        ResponseEntity<String> response = null;
        try {

            response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<String>() {

                    }
            );

        } catch (HttpClientErrorException e) {
            log.error("HTTP client Error: ", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error ", e.getMessage());
        }


        return response.getBody();
    }
}
