package com.dezso.varga.pokerfoci.authentication.security;

/**
 * Created by dezso on 07.12.2017.
 */
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private TokenAuthenticationService tokenAuthenticationService;

    public JWTAuthenticationFilter(AuthenticationManager authManager, TokenAuthenticationService tokenAuthenticationService) {
        super(authManager);
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest req,
                                 HttpServletResponse res,
                                 FilterChain filterChain)
            throws IOException, ServletException {
        Authentication authentication = tokenAuthenticationService.getAuthentication(req);
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(req,res);
    }
}