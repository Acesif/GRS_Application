package com.grs.grs_client.config;

import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.gateway.AuthGateway;
import com.grs.grs_client.model.OfficeInformation;
import com.grs.grs_client.model.UserDetails;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.BanglaConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthGateway authGateway;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails user = authGateway.login(BanglaConverter.convertToEnglish(name));

        if (user != null) {
            UserInformation userInformation = getUserInfo(user);
            List<GrantedAuthorityImpl> grantedAuthorities = user
                    .getPermissions()
                    .stream()
                    .map(permission -> {
                        return GrantedAuthorityImpl.builder()
                                .role(permission)
                                .build();
                    }).collect(Collectors.toList());
            UsernamePasswordAuthenticationToken token = new CustomAuthenticationToken(name, password, grantedAuthorities, userInformation);
            return token;
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private UserInformation getUserInfo(UserDetails user) {
        OfficeInformation officeInformation = user.getOfficeInformation();

        return UserInformation
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .userType(UserType.OISF_USER)
                .oisfUserType(user.getOisfUserType())
                .grsUserType(null)
                .officeInformation(officeInformation)
                .isAppealOfficer(user.getIsAppealOfficer())
                .isOfficeAdmin(user.getIsOfficeAdmin())
                .isCentralDashboardUser(user.getIsCentralDashboardUser())
                .isCellGRO(user.getIsCellGRO())
                .isMobileLogin(false)
                .build();
    }

}
