package com.grs.grs_client.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grs.grs_client.utils.BeanUtil;
import com.grs.grs_client.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.StringUtil;
import com.grs.grs_client.service.FcmService;
import io.jsonwebtoken.*;
import com.grs.grs_client.utils.CookieUtil;
@Slf4j
public class TokenAuthenticationServiceUtil {

    private final static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void addAuthentication(Authentication authentication,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException, ServletException {
        UserInformation userInformation;
        String name;
        Set<String> permissionNamesSet;
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            name = authentication.getName();
            permissionNamesSet = authentication.getAuthorities()
                    .stream()
                    .map(permission -> permission.getAuthority())
                    .collect(Collectors.toSet());
            userInformation = userDetails.getUserInformation();

        } catch (Exception e) {
            CustomAuthenticationToken token = (CustomAuthenticationToken) authentication;
            name = token.getName();
            permissionNamesSet = token.getAuthorities()
                    .stream()
                    .map(permission -> permission.getAuthority())
                    .collect(Collectors.toSet());
            userInformation = token.getUserInformation();
        }

        String deviceToken = request.getParameter("device_token");
        if (StringUtil.isValidString(deviceToken)) {
            FcmService fcmService = BeanUtil.bean(FcmService.class);
            fcmService.registerDeviceToken(deviceToken, name);
            userInformation.setIsMobileLogin(true);
        } else {
            userInformation.setIsMobileLogin(false);
        }
        String JWT = constuctJwtToken(name, permissionNamesSet, userInformation);
        Cookie cookie = new Cookie(Constant.HEADER_STRING, JWT);
        cookie.setMaxAge(Constant.COOKIE_EXPIRATION_TIME);
        response.addCookie(cookie);

        String redirectUrl = request.getParameter("redirectUrl");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            response.sendRedirect("/" + redirectUrl);
        } else {
            response.sendRedirect("/login/success");
        }

    }

    public static void addAuthenticationForMyGov(UserDetailsImpl userDetails,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        UserInformation userInformation;
        String name;
        Set<String> permissionNamesSet;
        try {
            permissionNamesSet = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            userInformation = userDetails.getUserInformation();
            name = userDetails.getUsername();

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        String deviceToken = request.getParameter("device_token");
        if (StringUtil.isValidString(deviceToken)) {
            FcmService fcmService = BeanUtil.bean(FcmService.class);
            fcmService.registerDeviceToken(deviceToken, name);
            userInformation.setIsMobileLogin(true);
        } else {
            userInformation.setIsMobileLogin(false);
        }
        String JWT = constuctJwtToken(name, permissionNamesSet, userInformation);
        Cookie cookie = new Cookie(Constant.HEADER_STRING, JWT);
        cookie.setMaxAge(Constant.COOKIE_EXPIRATION_TIME);
        response.addCookie(cookie);
        userInformation.setToken(JWT);

        response.sendRedirect("/login/success");

    }

    public static String constuctJwtToken(String username, Set<String> permissionNames, UserInformation userInformation) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(Constant.AUTHORITY, permissionNames);
        claims.put(Constant.USER_INFO, userInformation);
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + Constant.EXPIRATIONTIME))
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, Constant.SECRET)
                .compact();
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = CookieUtil.getValue(request, Constant.HEADER_STRING);
        if (token == null) {
            token = request.getHeader(Constant.HEADER_STRING);
        }
        if (token != null) {
            token = token.replace(Constant.TOKEN_PREFIX, "");
            Claims body = Jwts.parser()
                    .setSigningKey(Constant.SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            String username = body.getSubject();
            List<String> permissions = (ArrayList<String>) body.get(Constant.AUTHORITY);

            UserInformation userInformation = objectMapper.convertValue(body.get(Constant.USER_INFO), UserInformation.class);

            List<GrantedAuthorityImpl> grantedAuthorities = permissions.stream()
                    .map(permission -> {
                        return GrantedAuthorityImpl.builder()
                                .role(permission)
                                .build();
                    }).collect(Collectors.toList());

            return username != null ?
                    new CustomAuthenticationToken(username, null, grantedAuthorities, userInformation) :
                    null;
        }
        return null;
    }

    public static Authentication getMyGovAuthentication(HttpServletRequest request, String token) {
        token = token.replace(Constant.TOKEN_PREFIX, "");
        Claims body = Jwts.parser()
                .setSigningKey(Constant.SECRET)
                .parseClaimsJws(token)
                .getBody();

        String username = body.getSubject();
        List<String> permissions = (ArrayList<String>) body.get(Constant.AUTHORITY);

        UserInformation userInformation = objectMapper.convertValue(body.get(Constant.USER_INFO), UserInformation.class);

        List<GrantedAuthorityImpl> grantedAuthorities = permissions.stream()
                .map(permission -> {
                    return GrantedAuthorityImpl.builder()
                            .role(permission)
                            .build();
                }).collect(Collectors.toList());

        return username != null ?
                new CustomAuthenticationToken(username, null, grantedAuthorities, userInformation) :
                null;
    }

}

