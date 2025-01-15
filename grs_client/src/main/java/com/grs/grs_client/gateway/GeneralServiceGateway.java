package com.grs.grs_client.gateway;


import com.grs.grs_client.model.*;

import com.grs.grs_client.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Service
@Slf4j
public class GeneralServiceGateway extends BaseRestTemplate{
    String GRS_CORE_CONTEXT_PATH = "/grs_server";


    public Integer getMaximumFileSize() throws IllegalAccessException {


        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/generalSettings/getMaximumFileSize";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);


        HttpEntity<?> entity = new HttpEntity<>(headers);


        ResponseEntity<Integer> response = null;
        try {

            response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<Integer>() {

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


    public String getAllowedFileTypes() throws IllegalAccessException {


        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/generalSettings/getAllowedFileTypes";

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



    public String getAllowedFileSizeLabel() throws IllegalAccessException {


        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/generalSettings/getAllowedFileSizeLabel";

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



    public String getAllowedFileTypesLabel() throws IllegalAccessException {


        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/generalSettings/getAllowedFileTypesLabel";

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


    public String getSettingsValueByFieldName( String name) {

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/api/generalSettings/getSettingsValueByFieldName";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        url = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("name", name)
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, new ParameterizedTypeReference<String>() {
                });
        return response.getBody();


    }

}
