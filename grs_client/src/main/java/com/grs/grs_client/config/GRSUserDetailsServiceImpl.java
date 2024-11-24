package com.grs.grs_client.config;

import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.gateway.AuthGateway;
import com.grs.grs_client.model.UserDetails;
import com.grs.grs_client.model.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GRSUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AuthGateway authGateway;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = authGateway.login(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        List<GrantedAuthorityImpl> grantedAuthorities = user.getPermissions().stream()
                .map(permission -> {
                    return GrantedAuthorityImpl.builder()
                            .role(permission)
                            .build();
                }).collect(Collectors.toList());

        UserInformation userInformation = getUserInfo(user);

        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .grantedAuthorities(grantedAuthorities)
                .userInformation(userInformation)
                .isAccountAuthenticated(true)
                .build();
    }

    private UserInformation getUserInfo(UserDetails user) {
        return UserInformation
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .userType(UserType.SYSTEM_USER)
                .oisfUserType(null)
                .isOfficeAdmin(false)
                .isAppealOfficer(false)
                .isCellGRO(false)
                .grsUserType(user.getGrsUserType())
                .build();
    }
}
