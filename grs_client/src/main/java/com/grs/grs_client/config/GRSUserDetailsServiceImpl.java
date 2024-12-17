package com.grs.grs_client.config;

import com.grs.grs_client.enums.UserType;
import com.grs.grs_client.gateway.AuthGateway;
import com.grs.grs_client.model.LoginRequest;
import com.grs.grs_client.model.LoginResponse;
import com.grs.grs_client.model.UserDetails;
import com.grs.grs_client.model.UserInformation;
import com.grs.grs_client.utils.BanglaConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GRSUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AuthGateway authGateway;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ServletContext mcontext;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = "";

        if (mcontext != null) {
            password = (String) mcontext.getAttribute("the_pass");
        }
        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password).build();

        LoginResponse user = authGateway.login(loginRequest);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        List<GrantedAuthorityImpl> grantedAuthorities = user.getAuthorities().stream()
                .map(permission -> {
                    return GrantedAuthorityImpl.builder()
                            .role(permission)
                            .build();
                }).collect(Collectors.toList());

        UserInformation userInformation = getUserInfo(user);

        return UserDetailsImpl.builder()
                .username(user.getUserInformation().getUsername())
                .password(password)
                .grantedAuthorities(grantedAuthorities)
                .userInformation(userInformation)
                .isAccountAuthenticated(true)
                .build();
    }

    private UserInformation getUserInfo(LoginResponse user) {
        return user.getUserInformation();
    }
}
