package com.grs.api.gateway;

import com.grs.core.domain.grs.Complainant;
import com.grs.core.domain.grs.SuperAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperAdminGateway {

    private final RestTemplate restTemplate;
    private final String GRS_CORE_CONTEXT_PATH = "/grs_security/auth/superAdmin";

    private String getUrl() {
        return "http://localhost:8088";
    }

    public SuperAdmin findByUsername(String username){

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findByUsername";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("username", "{username}")
                .buildAndExpand(username)
                .toUriString();

        try {
            ResponseEntity<SuperAdmin> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    SuperAdmin.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }

    }

    public SuperAdmin findByUsernameAndPassword(String username, String password){

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findByUsernameAndPassword";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("Username", "{username}")
                .queryParam("password", "{password}")
                .buildAndExpand(username, password)
                .toUriString();

        try {
            ResponseEntity<SuperAdmin> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    SuperAdmin.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public List<SuperAdmin> findByUserRoleId(long role) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findByUserRoleId";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("role", "{role}")
                .buildAndExpand(role)
                .toUriString();

        try {
            ResponseEntity<SuperAdmin[]> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    SuperAdmin[].class
            );
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public SuperAdmin findByPhoneNumber(String phoneNumber) {

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findByPhoneNumber";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("phoneNumber", "{phoneNumber}")
                .buildAndExpand(phoneNumber)
                .toUriString();

        try {
            ResponseEntity<SuperAdmin> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    SuperAdmin.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }

    }

    public Integer countByUsername(String username) {

        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/countByUsername";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("username", "{username}")
                .buildAndExpand(username)
                .toUriString();

        try {
            ResponseEntity<Integer> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    Integer.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public SuperAdmin save(SuperAdmin superAdmin) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/save";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SuperAdmin> entity = new HttpEntity<>(superAdmin, headers);

            ResponseEntity<SuperAdmin> response = restTemplate.postForEntity(url, entity, SuperAdmin.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public SuperAdmin findOne(Long id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findOne/" + id;

        try {
            ResponseEntity<SuperAdmin> response = restTemplate.getForEntity(url, SuperAdmin.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public List<SuperAdmin> findAll() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findAll";

        try {
            ResponseEntity<SuperAdmin[]> response = restTemplate.getForEntity(url, SuperAdmin[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }
}
