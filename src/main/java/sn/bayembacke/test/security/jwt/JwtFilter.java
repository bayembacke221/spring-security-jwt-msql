package sn.bayembacke.test.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import sn.bayembacke.test.dao.UserRepository;
import sn.bayembacke.test.security.SecurityParams;
import sn.bayembacke.test.service.UserDetailsServiceImpl;

import static java.util.List.of;
import static java.util.Optional.ofNullable;


@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtFilter(JwtUtil jwtTokenUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(SecurityParams.JWT_HEADER_NAME);
        String username = null;
        String jwt = null;
        if(header != null && header.startsWith(SecurityParams.HEADER_PREFIX)){
            jwt = header.substring(7);
            username = jwtTokenUtil.extractUsername(jwt);
        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if(jwtTokenUtil.validateToken(jwt,userDetails.getUsername())){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
        }
        chain.doFilter(request,response);
    }
}