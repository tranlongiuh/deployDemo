package com.iuh.canteen.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    private final UserSecurityService userSecurityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = jwtUtil.getJwtFromRequest(request);
        if (jwt != null && jwtUtil.validateToken(jwt)) {
            String username = jwtUtil.extractUsername(jwt);
            System.err.println("username " + username);
            UserDetails user = userSecurityService.loadUserByUsername(username);
            System.err.println("userDetails " + user.toString());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            SecurityContextHolder.getContext()
                                 .setAuthentication(authentication);
            System.err.println("getAuthorities isPresent = " + user.getAuthorities()
                                                                   .stream()
                                                                   .findFirst()
                                                                   .isPresent());
        } else {
            System.err.println("Invalid JWT token or token not found.");
        }
        // Proceed with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
