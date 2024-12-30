package com.grs.grs_client.config;

import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
@Component
@WebFilter(urlPatterns = "/api/*")
public class ApiForwardingFilter implements Filter {

    private static final String SERVER_BASE_URL = "http://localhost:8081/grs_server";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Ensure UTF-8 encoding for the response
            httpResponse.setCharacterEncoding("UTF-8");

            String requestURI = httpRequest.getRequestURI();

            if (requestURI.startsWith("/api/")) {
                String serverUrl = SERVER_BASE_URL + requestURI;
                String method = httpRequest.getMethod();

                try {
                    ResponseEntity<String> responseEntity;
                    if ("GET".equalsIgnoreCase(method)) {
                        responseEntity = forwardGetRequest(serverUrl, httpRequest);
                    } else if ("POST".equalsIgnoreCase(method)) {
                        responseEntity = forwardPostRequest(serverUrl, httpRequest);
                    } else {
                        httpResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        httpResponse.getWriter().write("Unsupported HTTP method: " + method);
                        return;
                    }

                    // Write response with UTF-8 encoding
                    httpResponse.setStatus(responseEntity.getStatusCodeValue());
                    httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    String responseBody = new String(responseEntity.getBody().getBytes(), "UTF-8");
                    httpResponse.getWriter().write(responseBody);
                } catch (HttpClientErrorException e) {
                    logError(httpResponse, e.getStatusCode(), e.getResponseBodyAsString());
                } catch (Exception e) {
                    logError(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    protected String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return userInformation.getToken();
    }

    private ResponseEntity<String> forwardGetRequest(String serverUrl, HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + getToken());

        // Append query parameters if present
        String queryString = httpRequest.getQueryString();
        if (queryString != null) {
            serverUrl = serverUrl + "?" + queryString;
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl, HttpMethod.GET, entity, String.class);
    }

    private ResponseEntity<String> forwardPostRequest(String serverUrl, HttpServletRequest httpRequest) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getToken());

        String contentType = httpRequest.getContentType();
        if (contentType != null) {
            headers.setContentType(MediaType.parseMediaType(contentType));
        }

        HttpEntity<?> entity;
        if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            // Handle raw JSON
            String requestBody = extractRequestBody(httpRequest);
            entity = new HttpEntity<>(requestBody, headers);
        } else if (contentType != null && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            // Handle form data
            Map<String, String> formData = extractFormData(httpRequest);
            entity = new HttpEntity<>(formData, headers);
        } else if (contentType != null && contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            // Handle multipart file upload
            entity = new HttpEntity<>(httpRequest.getInputStream(), headers);
        } else {
            // Default to raw body
            String requestBody = extractRequestBody(httpRequest);
            entity = new HttpEntity<>(requestBody, headers);
        }

        return restTemplate.exchange(serverUrl, HttpMethod.POST, entity, String.class);
    }

    private Map<String, String> extractFormData(HttpServletRequest request) throws IOException {
        Map<String, String> formData = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            formData.put(paramName, paramValue);
        }
        return formData;
    }

    private String extractRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private void logError(HttpServletResponse httpResponse, HttpStatus status, String message) throws IOException {
        httpResponse.setStatus(status.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpResponse.setCharacterEncoding("UTF-8"); // Ensure UTF-8 for error messages
        httpResponse.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    @Override
    public void destroy() {
        // No cleanup required
    }
}

