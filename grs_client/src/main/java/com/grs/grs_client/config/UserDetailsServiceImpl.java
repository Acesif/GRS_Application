package com.grs.grs_client.config;

import com.grs.grs_client.gateway.AuthGateway;
import com.grs.grs_client.model.LoginResponse;
import com.grs.grs_client.model.UserDetails;
import com.grs.grs_client.model.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.grs.grs_client.utils.BanglaConverter;
import com.grs.grs_client.enums.UserType;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AuthGateway authGateway;


    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginResponse loginResponse = authGateway.login(BanglaConverter.convertToEnglish(username), null);
        if (loginResponse.getUserInformation() == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        List<String> permissions = new ArrayList(){{
            add("ADD_PUBLIC_GRIEVANCES");
            add("DO_APPEAL");
        }};
        List<GrantedAuthorityImpl> grantedAuthorities = loginResponse.getAuthorities().stream()
                .map(permission -> {
                    return GrantedAuthorityImpl.builder()
                            .role(permission)
                            .build();
                }).collect(Collectors.toList());

//        UserInformation userInformation = UserInformation
//                .builder()
//                .userId(user.getId())
//                .username(user.getName())
//                .userType(UserType.COMPLAINANT)
//                .officeInformation(null)
//                .oisfUserType(null)
//                .isAppealOfficer(false)
//                .build();
        UserInformation userInformation = loginResponse.getUserInformation();

        return UserDetailsImpl.builder()
                .username(userInformation.getUsername())
//                .password(user.getPassword())
                .password(null)
                .isAccountAuthenticated(true)
                .grantedAuthorities(grantedAuthorities)
                .userInformation(userInformation)
                .build();
    }
}
