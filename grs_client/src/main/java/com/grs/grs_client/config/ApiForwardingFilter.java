package com.grs.grs_client.config;

import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
@Component
@WebFilter(urlPatterns = "/api/*")
public class ApiForwardingFilter implements Filter {

    private static final String SERVER_BASE_URL = "http://localhost:8088/grs_server";

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

            httpResponse.setCharacterEncoding("UTF-8");

            String requestURI = httpRequest.getRequestURI();

            if (requestURI.startsWith("/api/")) {
                String serverUrl = SERVER_BASE_URL + requestURI;
                String method = httpRequest.getMethod();
                try {
                    ResponseEntity<byte[]> responseEntity;
                    if ("GET".equalsIgnoreCase(method)) {
                        responseEntity = forwardGetRequest(serverUrl, httpRequest);
                    } else if ("POST".equalsIgnoreCase(method)) {
                        responseEntity = forwardPostRequest(serverUrl, httpRequest);
                    } else if ("DELETE".equalsIgnoreCase(method)) {
                        responseEntity = forwardDeleteRequest(serverUrl, httpRequest);
                    } else {
                        httpResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        httpResponse.getWriter().write("Unsupported HTTP method: " + method);
                        return;
                    }

                    // Write response
                    httpResponse.setStatus(responseEntity.getStatusCodeValue());
                    responseEntity.getHeaders().forEach((key, values) -> {
                        if (!"Transfer-Encoding".equalsIgnoreCase(key)) { // Exclude headers that might cause issues
                            for (String value : values) {
                                httpResponse.addHeader(key, value);
                            }
                        }
                    });

                    // Write binary response directly to output stream
                    byte[] responseBody = responseEntity.getBody();
                    if (responseBody != null) {
                        httpResponse.getOutputStream().write(responseBody);
                    }
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

    private boolean extractAuthorizationFromCookie(String cookieHeader) {
        String[] cookies = cookieHeader.split("; ");
        for (String cookie : cookies) {
            if (cookie.startsWith("Authorization=")) {
                return true;
            }
        }
        return false;
    }

    protected String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInformation userInformation = Utility.extractUserInformationFromAuthentication(authentication);
        return userInformation.getToken();
    }

    private ResponseEntity<byte[]> forwardGetRequest(String serverUrl, HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String cookieHeader = httpRequest.getHeader("Cookie");

        if (extractAuthorizationFromCookie(cookieHeader)) {
            headers.add( "Authorization", "Bearer " + getToken());
        }

        // Check if the incoming request contains an Authorization header
        String incomingAuthHeader = httpRequest.getHeader("Authorization");
        if (incomingAuthHeader != null && !incomingAuthHeader.isEmpty()) {
            headers.add("Authorization", incomingAuthHeader);
        }

        // Append query parameters if present
        String queryString = httpRequest.getQueryString();
        if (queryString != null) {
            serverUrl = serverUrl + "?" + queryString;
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl, HttpMethod.GET, entity, byte[].class);
    }




    public ResponseEntity<byte[]> forwardPostRequest(String serverUrl, HttpServletRequest httpRequest) throws IOException, ServletException {
        HttpHeaders headers = new HttpHeaders();
        String contentType = httpRequest.getContentType();

        if (contentType != null) {
            headers.setContentType(MediaType.parseMediaType(contentType));
        }

        String cookieHeader = httpRequest.getHeader("Cookie");

        if (extractAuthorizationFromCookie(cookieHeader)) {
            headers.add( "Authorization", "Bearer " + getToken());
        }

        HttpEntity<?> entity;
        if (contentType != null && MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(contentType))) {
            String requestBody = extractRequestBody(httpRequest);
            entity = new HttpEntity<>(requestBody, headers);
        } else if (contentType != null && MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(MediaType.parseMediaType(contentType))) {
            Map<String, String> formData = extractFormData(httpRequest);
            entity = new HttpEntity<>(formData, headers);
        } else if (contentType != null && MediaType.MULTIPART_FORM_DATA.isCompatibleWith(MediaType.parseMediaType(contentType))) {
            entity = buildMultipartEntity(httpRequest, headers);
        } else {
            String requestBody = extractRequestBody(httpRequest);
            entity = new HttpEntity<>(requestBody, headers);
        }

        return restTemplate.exchange(serverUrl, HttpMethod.POST, entity, byte[].class);
    }

    private HttpEntity<MultiValueMap<String, Object>> buildMultipartEntity(HttpServletRequest request, HttpHeaders headers) throws IOException, ServletException {
        MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
        Collection<Part> parts = request.getParts();

        for (Part part : parts) {
            String contentDisposition = part.getHeader("content-disposition");
            if (contentDisposition != null && contentDisposition.contains("filename")) {
                multipartData.add(part.getName(), new InputStreamResource(part.getInputStream()) {
                    @Override
                    public String getFilename() {
                        return part.getSubmittedFileName();
                    }

                    @Override
                    public long contentLength() {
                        return part.getSize();
                    }
                });
            } else {
                multipartData.add(part.getName(), readInputStreamAsString(part.getInputStream()));
            }
        }

        return new HttpEntity<>(multipartData, headers);
    }

    private String readInputStreamAsString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private ResponseEntity<byte[]> forwardDeleteRequest(String serverUrl, HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String cookieHeader = httpRequest.getHeader("Cookie");
        if (cookieHeader != null && extractAuthorizationFromCookie(cookieHeader)) {
            headers.add("Authorization", "Bearer " + getToken());
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(serverUrl, HttpMethod.DELETE, entity, byte[].class);
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

