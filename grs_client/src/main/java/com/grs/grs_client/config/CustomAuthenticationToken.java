package com.grs.grs_client.config;

import com.grs.grs_client.model.UserInformation;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private UserInformation userInformation;

    public CustomAuthenticationToken(Object principal,
                                     Object credentials,
                                     UserInformation userInformation) {
        super(principal, credentials);
        this.userInformation = userInformation;
    }

    public CustomAuthenticationToken(Object principal,
                                     Object credentials,
                                     Collection<? extends GrantedAuthority> authorities,
                                     UserInformation userInformation) {
        super(principal, credentials, authorities);
        this.userInformation = userInformation;
    }

}
