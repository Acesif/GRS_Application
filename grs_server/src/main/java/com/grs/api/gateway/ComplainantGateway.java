package com.grs.api.gateway;

import com.grs.core.domain.grs.Complainant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplainantGateway {

    private final RestTemplate restTemplate;
    private final String GRS_CORE_CONTEXT_PATH = "/grs_security/auth/complainant";

    private String getUrl() {
        return "http://localhost:8088";
    }

    public Complainant findByUsername(String username) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findByUsername";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("username", "{username}")
                .buildAndExpand(username)
                .toUriString();

        try {
            ResponseEntity<Complainant> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    Complainant.class
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

    public Complainant findByUsernameAndPassword(String username, String password) {
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
            ResponseEntity<Complainant> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    Complainant.class
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

    public Complainant findByPhoneNumber(String phoneNumber) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findByPhoneNumber/"+phoneNumber;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Complainant> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Complainant.class
            );
            System.out.println(response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public List<Complainant> findByPhoneNumberIsContaining(String phoneNumber) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findByPhoneNumberIsContaining";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("phoneNumber", "{phoneNumber}")
                .buildAndExpand(phoneNumber)
                .toUriString();

        try {
            ResponseEntity<Complainant[]> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    Complainant[].class
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

    public long count() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/count";

        try {
            ResponseEntity<Long> response = restTemplate.getForEntity(url, Long.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public Complainant findOne(Long id) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findOne/" + id;

        try {
            ResponseEntity<Complainant> response = restTemplate.getForEntity(url, Complainant.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public List<Complainant> findAll() {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/findAll";

        try {
            ResponseEntity<Complainant[]> response = restTemplate.getForEntity(url, Complainant[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }

    public Complainant save(Complainant complainant) {
        String url = getUrl() + GRS_CORE_CONTEXT_PATH + "/save";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Complainant> entity = new HttpEntity<>(complainant, headers);

            ResponseEntity<Complainant> response = restTemplate.postForEntity(url, entity, Complainant.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client Error: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());
            throw e;
        }
    }
}
