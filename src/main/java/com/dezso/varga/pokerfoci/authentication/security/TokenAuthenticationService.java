package com.dezso.varga.pokerfoci.authentication.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security
        .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

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

    public Authentication getAuthentication(HttpServletRequest request) throws ServletException {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            token = token.replace(TOKEN_PREFIX+" ", "");
            String user = "";
            try {
                user = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
            } catch(Exception ex) {
//                throw new BgException("Authorization token expired or invalid", HttpStatus.UNAUTHORIZED.value());
                return null;
            }
            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                    null;
        }
        return null;
    }
}