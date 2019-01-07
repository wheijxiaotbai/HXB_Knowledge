package demo.demo1.auth.security;

import demo.demo1.User.service.UserService;
import demo.demo1.auth.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String token = request.getHeader(JwtUtil.HEADER_STRING);
        if (token != null && token.startsWith(JwtUtil.TOKEN_PREFIX)) {
            token = token.replace(JwtUtil.TOKEN_PREFIX, "");
            try {
                String id = jwtUtil.parseTokenClaims(token).getSubject();
                String username = userService.getUserById(UUID.fromString(id)).getUsername();
                if (null != id && SecurityContextHolder.getContext().getAuthentication() == null) {
                    logger.debug("Checking token for user {}", id);
                    // In security, use uuid as username.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(token) && userDetails != null) {
                        // create authentication
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        // set authentication
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // put authentication into context holder
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                logger.debug("Check token failed {}", e.getMessage());
            }
        }

        chain.doFilter(request, response);

    }

}
