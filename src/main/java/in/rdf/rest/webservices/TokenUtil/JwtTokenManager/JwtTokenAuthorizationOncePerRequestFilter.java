package in.rdf.rest.webservices.TokenUtil.JwtTokenManager;

import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenAuthorizationOncePerRequestFilter extends OncePerRequestFilter {

  private final Logger LOGGER = LoggerFactory
      .getLogger(JwtTokenAuthorizationOncePerRequestFilter.class);

  @Autowired
  private UserDetailsService jwtInMemoryUserDetailsService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Value("${jwt.http.request.header}")
  private String tokenHeader;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    LOGGER.info("Enter JwtTokenAuthorizationOncePerRequestFilter.doFilterInternal()");

    LOGGER.info("****** 1 Authentication Request For '{}'", request.getRequestURL());

    final String requestTokenHeader = request.getHeader(this.tokenHeader);
    LOGGER.info("****** 2 Request token header '{}'", requestTokenHeader);

    String username = null;
    String jwtToken = null;
    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      LOGGER.info(
          "****** Enter if - requestTokenHeader != null && requestTokenHeader.startsWith(\"Bearer \")");
      jwtToken = requestTokenHeader.substring(7); //Remove prefix Bearer from token
      LOGGER.info("****** 3 Request token header '{}'", jwtToken);

      try {
        LOGGER.info("****** 4 Username obtained from token '{}'", username);
        username = jwtTokenUtil.getUsernameFromToken(jwtToken);

      } catch (IllegalArgumentException e) {
        logger.error("5 JWT_TOKEN_UNABLE_TO_GET_USERNAME", e);
      } catch (ExpiredJwtException e) {
        logger.warn("6 JWT_TOKEN_EXPIRED", e);
      }
    } else {
      LOGGER.info(
          "****** Enter else - requestTokenHeader != null && requestTokenHeader.startsWith(\"Bearer \")");
      logger.info("7 JWT_TOKEN_DOES_NOT_START_WITH_BEARER_STRING");
    }

    LOGGER.info("JWT_TOKEN_USERNAME_VALUE '{}'", username);

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      LOGGER.info(
          "##### 8 Enter if - username != null && SecurityContextHolder.getContext().getAuthentication() == null");
      LOGGER.info("##### 9 username '{}'", username);
      LOGGER.info("##### 10 SecurityContextHolder.getContext().getAuthentication() '{}'",
          SecurityContextHolder.getContext().getAuthentication());

      UserDetails userDetails = this.jwtInMemoryUserDetailsService.loadUserByUsername(username);
      LOGGER.info("User details in JwtTokenAuthorizationOncePerRequestFilter.doFilterInternal() userDetails.getUsername() "+ userDetails.getUsername());
      LOGGER.info(
          "User details in JwtTokenAuthorizationOncePerRequestFilter.doFilterInternal() userDetails.getPassword() "
              + userDetails.getPassword());
      LOGGER.info(
          "User details in JwtTokenAuthorizationOncePerRequestFilter.doFilterInternal() userDetails.getAuthorities() "
              + userDetails.getAuthorities());
      LOGGER.info(
          "User details in JwtTokenAuthorizationOncePerRequestFilter.doFilterInternal() userDetails.isAccountNonExpired() "
              + userDetails.isAccountNonExpired());
      LOGGER.info(
          "User details in JwtTokenAuthorizationOncePerRequestFilter.doFilterInternal() userDetails.isAccountNonLocked() "
              + userDetails.isAccountNonLocked());
      LOGGER.info(
          "User details in JwtTokenAuthorizationOncePerRequestFilter.doFilterInternal() userDetails.isCredentialsNonExpired() "
              + userDetails.isCredentialsNonExpired());
      LOGGER.info(
          "User details in JwtTokenAuthorizationOncePerRequestFilter.doFilterInternal() userDetails.isEnabled() "
              + userDetails.isEnabled());

      if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
        LOGGER.info("Enter if - jwtTokenUtil.validateToken(jwtToken, userDetails) ");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }

    chain.doFilter(request, response);
  }
}


