package com.dezso.varga.pokerfoci.security;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security
        .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class TokenAuthenticationService {
    static final long EXPIRATION_TIME = 864_000_000; // 10 days
    static final String SECRET = "secretkey";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";

    public void addAuthentication(HttpServletResponse res, String username) {
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
    }

    private Set<Role> getRoles(Claims claims) {
        Set rolesSet = new HashSet();
        String roles = (String) claims.get("roles");
        roles = roles.replace("[", "").replace("]", "");
        String[] roleNames = roles.split(",");

        for (String aRoleName : roleNames) {
            rolesSet.add(new Role(aRoleName));
        }
        return rolesSet;
    }

    private UserDetails getUserDetails(String token) {
        Claims claims;
        Set roles = new HashSet();
        String email = "";
        Long id = 0L;
        String []subject;
        Account account = new Account();
        if (token != null) {
            token = token.replace(TOKEN_PREFIX+" ", "");

            try {
                claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
                subject = claims.getSubject().split(",");
                id = Long.parseLong(subject[0]);
                email = subject[1];
                roles = getRoles(claims);
            } catch(Exception ex) {
                return null;
            }
        }
        account.setId(id);
        account.setUsername(email);
        account.setRoles(roles);
        return account;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws ServletException {
        String token = request.getHeader(HEADER_STRING);
        UserDetails userDetails = this.getUserDetails(token);
        return userDetails.getUsername() != null ?
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) :
                null;
    }
}