package com.jwt.crudbd.security.jwt;

import com.jwt.crudbd.security.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    private final static Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try{
            String token =  getToken(request);

            if (token != null && jwtProvider.validateToken(token)) {
                String username = jwtProvider.getNombreUsuarioFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e){
            logger.error("Error en el método doFilter"+e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer"))
            return header.replace("Bearer ", "");
        return null;
    }
}
