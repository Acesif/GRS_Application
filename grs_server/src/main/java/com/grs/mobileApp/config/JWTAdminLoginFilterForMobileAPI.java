package com.grs.mobileApp.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grs.api.config.security.GrantedAuthorityImpl;
import com.grs.api.config.security.OISFUserDetailsServiceImpl;
import com.grs.api.config.security.TokenAuthenticationServiceUtil;
import com.grs.api.config.security.UserDetailsImpl;
import com.grs.api.model.UserInformation;
import com.grs.api.sso.LoginRequest;
import com.grs.core.dao.GrsRoleDAO;
import com.grs.core.domain.doptor.User;
import com.grs.core.domain.grs.GrsRole;
import com.grs.mobileApp.dto.MobileAdminLoginResponse;
import com.grs.mobileApp.dto.MobileResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JWTAdminLoginFilterForMobileAPI extends AbstractAuthenticationProcessingFilter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String USERNAME_REQUEST_PARAM = "username";
    private final String PASSWORD_REQUEST_PARAM = "password";

    private final OISFUserDetailsServiceImpl oisfUserDetailsService;

    private final GrsRoleDAO grsRoleDAO;
    private final String nothiMobileUserVerifyUrl;
    private final boolean tokenStatus;


    public JWTAdminLoginFilterForMobileAPI(String url,
                                           AuthenticationManager authManager,
                                           BCryptPasswordEncoder bCryptPasswordEncoder,
                                           OISFUserDetailsServiceImpl oisfUserDetailsService,
                                           GrsRoleDAO grsRoleDAO,
                                           String mobileAdminVerify,
                                           boolean tokenStatus) {
        super(new AntPathRequestMatcher(url, "POST"));
        this.grsRoleDAO = grsRoleDAO;
        setAuthenticationManager(authManager);
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.oisfUserDetailsService = oisfUserDetailsService;
        this.nothiMobileUserVerifyUrl = mobileAdminVerify;
        this.tokenStatus = tokenStatus;
    }
    @Getter
    @Setter
    private static ResponseEntity<String> responseBody;

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LoginRequest authRequest = objectMapper.readValue(req.getInputStream(), LoginRequest.class);

            String username = authRequest.getUsername();
            String password = authRequest.getPassword();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("api-version", "1");

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("username", username);
            formData.add("password", password);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    nothiMobileUserVerifyUrl,
                    requestEntity,
                    String.class
            );
            if (responseEntity == null){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        Collections.emptyList()
                );

                setResponseBody(null);
                return authToken;
            }

            else if (responseEntity.getStatusCode() == HttpStatus.OK) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        Collections.emptyList()
                );

                setResponseBody(responseEntity);
                return authToken;
            } else {
                throw new BadCredentialsException("Authentication failed");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading request body", e);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Authentication failed", e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException {
        
//        String username = authentication.getName();
//        Object principal = authentication.getPrincipal();

//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Set<String> permissionNamesSet = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        
        ResponseEntity<String> responseEntity = getResponseBody();
        String responseBody = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MobileAdminLoginResponse responseMap = objectMapper.readValue(responseBody, MobileAdminLoginResponse.class);

        User user = responseMap.getData().getUser();
//        List<OfficeInfo> officeInfos = responseMap.getUser_info().getOffice_info();
//        EmployeeInfo employeeInfo = responseMap.getUser_info().getEmployee_info();
//        HashMap<String, OfficeOrganogram> organogramInfo = responseMap.getUser_info().getOrganogram_info();

        UserInformation userInformation = this.oisfUserDetailsService.getUserInfo(responseMap.getData());
        String roleName = null;
        if (userInformation.getGrsUserType() != null) {
            roleName = userInformation.getGrsUserType().name();
        } else {
            roleName = userInformation.getOisfUserType().name();
        }
        GrsRole grsRole = this.grsRoleDAO.findByRole(roleName);
        List<GrantedAuthorityImpl> grantedAuthorities = grsRole
                .getPermissions()
                .stream()
                .map(permission -> {
                    return GrantedAuthorityImpl.builder()
                            .role(permission.getName())
                            .build();
                }).collect(Collectors.toList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .username(user.getUsername())
                .isAccountAuthenticated(true)
                .grantedAuthorities(grantedAuthorities).userInformation(userInformation).build();


        try {
            ObjectMapper responseMapper = new ObjectMapper();
            Map<String, Object> responseData = responseMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

            Map<String,Object> data = new HashMap<>();
            data.put("user_info",responseData.get("data"));
            data.put("user_type",userInformation.getOisfUserType());

            if (tokenStatus){
                if(user.getUsername().equals("100000006843")){
                    data.put("token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMDAwMDAwMDA1NTUiLCJwZXJtaXNzaW9ucyI6W10sInVzZXJfaW5mbyI6eyJ1c2VySWQiOjUsInVzZXJuYW1lIjoiMjAwMDAwMDAwNTU1IiwidXNlclR5cGUiOiJPSVNGX1VTRVIiLCJvaXNmVXNlclR5cGUiOiJIRUFEX09GX09GRklDRSIsImdyc1VzZXJUeXBlIjpudWxsLCJvZmZpY2VJbmZvcm1hdGlvbiI6eyJvZmZpY2VJZCI6MjgsIm9mZmljZU5hbWVCYW5nbGEiOiLgpq7gpqjgp43gpqTgp43gprDgpr_gpqrgprDgpr_gprfgpqYg4Kas4Ka_4Kat4Ka-4KaXICIsIm9mZmljZU5hbWVFbmdsaXNoIjoiTWluaXN0cnkgRGl2aXNpb24iLCJvZmZpY2VNaW5pc3RyeUlkIjo0LCJvZmZpY2VPcmlnaW5JZCI6NDIsIm5hbWUiOiLgpq7gp4vgpoMg4Kau4Ka-4Ka54Kas4KeB4KasIOCmueCni-CmuOCnh-CmqCAiLCJkZXNpZ25hdGlvbiI6IuCmruCmqOCnjeCmpOCnjeCmsOCmv-CmquCmsOCmv-Cmt-CmpiDgprjgpprgpr_gpqwiLCJlbXBsb3llZVJlY29yZElkIjo5MzM1NCwib2ZmaWNlVW5pdE9yZ2Fub2dyYW1JZCI6MTEyNTQsImxheWVyTGV2ZWwiOjEsImdlb0RpdmlzaW9uSWQiOjMsImdlb0Rpc3RyaWN0SWQiOjE4fSwiaXNBcHBlYWxPZmZpY2VyIjpmYWxzZSwiaXNPZmZpY2VBZG1pbiI6ZmFsc2UsImlzQ2VudHJhbERhc2hib2FyZFVzZXIiOnRydWUsImlzQ2VsbEdSTyI6ZmFsc2UsImlzTW9iaWxlTG9naW4iOmZhbHNlLCJpc015R292TG9naW4iOm51bGwsInRva2VuIjpudWxsfX0.IT7eNtV5bqK58pWJuQcG3RfrPgAGa5WkjHLZxf4vSq87KJUoWA2U65UsPfHbfKUFiqdW4g27EFqkhWQqZHTEMA");
                } else {
                    data.put("token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMDAwMDAwMDA1NTkiLCJwZXJtaXNzaW9ucyI6W10sInVzZXJfaW5mbyI6eyJ1c2VySWQiOjksInVzZXJuYW1lIjoiMjAwMDAwMDAwNTU5IiwidXNlclR5cGUiOiJPSVNGX1VTRVIiLCJvaXNmVXNlclR5cGUiOiJIRUFEX09GX09GRklDRSIsImdyc1VzZXJUeXBlIjpudWxsLCJvZmZpY2VJbmZvcm1hdGlvbiI6eyJvZmZpY2VJZCI6MjgsIm9mZmljZU5hbWVCYW5nbGEiOiLgpq7gpqjgp43gpqTgp43gprDgpr_gpqrgprDgpr_gprfgpqYg4Kas4Ka_4Kat4Ka-4KaXICIsIm9mZmljZU5hbWVFbmdsaXNoIjoiTWluaXN0cnkgRGl2aXNpb24iLCJvZmZpY2VNaW5pc3RyeUlkIjo0LCJvZmZpY2VPcmlnaW5JZCI6NDIsIm5hbWUiOiLgpqEuIOCmtuCnh-CmliDgpobgpqzgp43gpqbgp4HgprAg4Kaw4Ka24KeA4KamIiwiZGVzaWduYXRpb24iOiLgpq7gpqjgp43gpqTgp43gprDgpr_gpqrgprDgpr_gprfgpqYg4Ka44Kaa4Ka_4KasIiwiZW1wbG95ZWVSZWNvcmRJZCI6NTIyNDc4LCJvZmZpY2VVbml0T3JnYW5vZ3JhbUlkIjoxMTI1NCwibGF5ZXJMZXZlbCI6MSwiZ2VvRGl2aXNpb25JZCI6MywiZ2VvRGlzdHJpY3RJZCI6MTh9LCJpc0FwcGVhbE9mZmljZXIiOmZhbHNlLCJpc09mZmljZUFkbWluIjpmYWxzZSwiaXNDZW50cmFsRGFzaGJvYXJkVXNlciI6dHJ1ZSwiaXNDZWxsR1JPIjpmYWxzZSwiaXNNb2JpbGVMb2dpbiI6ZmFsc2UsImlzTXlHb3ZMb2dpbiI6bnVsbCwidG9rZW4iOm51bGx9fQ.Q7r5jJc_R9hCNCUVdXn7R___1JAbzVAdDOJPmZN7WDzOd-bhsC5lWGGS3-VOWKmLDH6kUu87b97P1owbKfLpcA");
                }
            } else {
                data.put("token", TokenAuthenticationServiceUtil.addAuthenticationForMyGovMobile(userDetails, request, response));
            }

            Map<String,Object> mobileResponse = new HashMap<>();
            mobileResponse.put("status","success");
            mobileResponse.put("data", data);

            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), mobileResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        if (getResponseBody() == null){
            Map<String,Object> mobileResponse = new HashMap<>();
            mobileResponse.put("status","error");
            mobileResponse.put("data", "n-doptor is not responding");

            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), mobileResponse);
            return;
        }

        MobileResponse error = MobileResponse.builder()
                .status("error")
                .data("Wrong username or password")
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.addHeader("content-type", "application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), error);
    }

}
