package com.fsse.ecommerce.config;

import com.fsse.ecommerce.domain.User;
import com.fsse.ecommerce.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FirebaseSecurityFilter extends OncePerRequestFilter {
    private Logger log = LoggerFactory.getLogger(FirebaseSecurityFilter.class);

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        verifyToken(request);
        filterChain.doFilter(request, response);
    }

    private void verifyToken(HttpServletRequest request) {
        FirebaseToken decodedToken = null;
        String token = getBearerToken(request);
        try {
            if (token != null) {
                decodedToken =
                        FirebaseAuth.getInstance().verifyIdToken(token);
            }
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            log.error("Firebase Exception", e);
        }
        if (decodedToken != null) {
            User user = userService.getUserByFirebaseUid(decodedToken.getUid());
            if (user == null) {
                user = userService.createUser(decodedToken);
            }


            PreAuthenticatedAuthenticationToken authentication =
                    new PreAuthenticatedAuthenticationToken(user, token, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private String getBearerToken(HttpServletRequest request) {
        String bearerToken = null;
        String authorization =
                request.getHeader("Authorization");
        if (authorization != null && !authorization.isEmpty() &&
                authorization.startsWith("Bearer ")) {
            bearerToken = authorization.substring(7);
        }
        return bearerToken;
    }
}