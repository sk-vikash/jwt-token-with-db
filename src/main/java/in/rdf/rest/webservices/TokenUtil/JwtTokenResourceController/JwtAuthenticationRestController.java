package in.rdf.rest.webservices.TokenUtil.JwtTokenResourceController;


import in.rdf.rest.webservices.TokenUtil.JwtTokenManager.JwtTokenUtil;
import in.rdf.rest.webservices.TokenUtil.JwtTokenManager.JwtUserDetails;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtAuthenticationRestController {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(JwtAuthenticationRestController.class);

  @Value("${jwt.http.request.header}")
  private String tokenHeader;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private UserDetailsService jwtInMemoryUserDetailsService;

  @RequestMapping(value = "${jwt.get.token.uri}", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(
      @RequestBody JwtTokenRequest authenticationRequest)
      throws AuthenticationException {
    LOGGER.info("Enter JwtAuthenticationRestController.createAuthenticationToken()",
        authenticationRequest);

    authenticate(authenticationRequest.getUsername(),
        authenticationRequest.getPassword()); //May be this authenticate() method is not needed.

    final UserDetails userDetails = jwtInMemoryUserDetailsService
        .loadUserByUsername(authenticationRequest.getUsername());
    LOGGER.info(
        "User details in JwtAuthenticationRestController.createAuthenticationToken() userDetails.getUsername() "
            + userDetails.getUsername());
    LOGGER.info(
        "User details in JwtAuthenticationRestController.createAuthenticationToken() userDetails.getPassword() "
            + userDetails.getPassword());
    LOGGER.info(
        "User details in JwtAuthenticationRestController.createAuthenticationToken() userDetails.getAuthorities() "
            + userDetails.getAuthorities());
    LOGGER.info(
        "User details in JwtAuthenticationRestController.createAuthenticationToken() userDetails.isAccountNonExpired() "
            + userDetails.isAccountNonExpired());
    LOGGER.info(
        "User details in JwtAuthenticationRestController.createAuthenticationToken() userDetails.isAccountNonLocked() "
            + userDetails.isAccountNonLocked());
    LOGGER.info(
        "User details in JwtAuthenticationRestController.createAuthenticationToken() userDetails.isCredentialsNonExpired() "
            + userDetails.isCredentialsNonExpired());
    LOGGER.info(
        "User details in JwtAuthenticationRestController.createAuthenticationToken() userDetails.isEnabled() "
            + userDetails.isEnabled());

    final String token = jwtTokenUtil.generateToken(userDetails);

    LOGGER.info("token " + token);
    return ResponseEntity.ok(new JwtTokenResponse(token));
  }

  @RequestMapping(value = "${jwt.refresh.token.uri}", method = RequestMethod.GET)
  public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
    LOGGER.info("Enter JwtAuthenticationRestController.refreshAndGetAuthenticationToken()");
    String authToken = request.getHeader(tokenHeader);
    LOGGER.info("Refresh authToken " + authToken);

    final String token = authToken.substring(7);
    LOGGER.info("Refresh token " + token);
    String username = jwtTokenUtil.getUsernameFromToken(token);
    LOGGER.info("The username obtained from toekn " + username);

    JwtUserDetails user = (JwtUserDetails) jwtInMemoryUserDetailsService
        .loadUserByUsername(username);

    if (jwtTokenUtil.canTokenBeRefreshed(token)) {
      String refreshedToken = jwtTokenUtil.refreshToken(token);
      return ResponseEntity.ok(new JwtTokenResponse(refreshedToken));
    } else {
      return ResponseEntity.badRequest().body(null);
    }
  }

  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
    LOGGER.info("Enter JwtAuthenticationRestController.handleAuthenticationException()");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  private void authenticate(String username, String password) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(password);

    try {
      LOGGER.info("Enter JwtAuthenticationRestController.authenticate() username " + username
          + " ,password " + password);
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(username, password));
      LOGGER.info("Exit JwtAuthenticationRestController.authenticate()");
    } catch (DisabledException e) {
      throw new AuthenticationException("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new AuthenticationException("INVALID_CREDENTIALS", e);
    }
  }
}

