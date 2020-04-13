package in.rdf.rest.webservices.TokenUtil.JwtTokenManager;

import java.io.IOException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtUnAuthorizedResponseAuthenticationEntryPoint implements AuthenticationEntryPoint,
    Serializable {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(JwtUnAuthorizedResponseAuthenticationEntryPoint.class);

  private static final long serialVersionUID = -8970718410437077606L;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    LOGGER.info("Enter JwtUnAuthorizedResponseAuthenticationEntryPoint.commence " + request);
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
        "You would need to provide the Jwt Token to Access This resource");
    LOGGER.info("Exit JwtUnAuthorizedResponseAuthenticationEntryPoint.commence ");
  }
}