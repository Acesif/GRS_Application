package com.grs.grs_client.config;
import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

public class CustomAuthenticationDetailSource implements AuthenticationDetailsSource<HttpServletRequest, String> {

    @Override
    public String buildDetails(HttpServletRequest context) {
        String pass = context.getParameter("password");
        context.getServletContext().setAttribute("the_pass", pass);
        return pass;
    }
}
