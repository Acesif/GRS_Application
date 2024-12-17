package com.grs.grs_client.config;

import com.grs.grs_client.gateway.AuthGateway;
import com.grs.grs_client.model.LoginRequest;
import com.grs.grs_client.model.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.grs.grs_client.utils.BanglaConverter;
import javax.servlet.ServletContext;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AuthGateway authGateway;
    @Autowired
    private ServletContext mcontext;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {


        String password = "";

        if (mcontext != null) {
            password = (String) mcontext.getAttribute("the_pass");
        }
        String userName = BanglaConverter.convertToEnglish(username);
        LoginRequest loginRequest = LoginRequest.builder()
                                        .username(userName)
                                        .password(password).build();

        LoginResponse user = authGateway.login(loginRequest);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        List<String> permissions = new ArrayList(){{
            add("ADD_PUBLIC_GRIEVANCES");
            add("DO_APPEAL");
        }};
        List<GrantedAuthorityImpl> grantedAuthorities = user.getAuthorities().stream()
                .map(permission -> {
                    return GrantedAuthorityImpl.builder()
                            .role(permission)
                            .build();
                }).collect(Collectors.toList());

        return UserDetailsImpl.builder()
                .username(user.getUserInformation().getUsername())
                .password(password)
                .isAccountAuthenticated(true)
                .grantedAuthorities(grantedAuthorities)
                .userInformation(user.getUserInformation())
                .build();
    }
}
