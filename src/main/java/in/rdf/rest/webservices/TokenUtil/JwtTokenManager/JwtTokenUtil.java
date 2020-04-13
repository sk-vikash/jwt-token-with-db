package in.rdf.rest.webservices.TokenUtil.JwtTokenManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil implements Serializable {

  static final String CLAIM_KEY_USERNAME = "sub";
  static final String CLAIM_KEY_CREATED = "iat";
  private static final Logger LOGGER = LoggerFactory
      .getLogger(JwtTokenUtil.class);
  private static final long serialVersionUID = -3301605591108950415L;
  private Clock clock = DefaultClock.INSTANCE;

  @Value("${jwt.signing.key.secret}")
  private String secret;

  @Value("${jwt.token.expiration.in.seconds}")
  private Long expiration;

  public String getUsernameFromToken(String token) {
    LOGGER.info("Enter JwtTokenUtil.getUsernameFromToken() ");
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getIssuedAtDateFromToken(String token) {
    LOGGER.info("Enter JwtTokenUtil.getIssuedAtDateFromToken() ");
    return getClaimFromToken(token, Claims::getIssuedAt);
  }

  public Date getExpirationDateFromToken(String token) {
    LOGGER.info("Enter JwtTokenUtil.getExpirationDateFromToken() ");
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    LOGGER.info("Enter JwtTokenUtil.getClaimFromToken() ");
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    LOGGER.info("Enter JwtTokenUtil.getAllClaimsFromToken() ");
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    LOGGER.info("Enter JwtTokenUtil.isTokenExpired() ");
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(clock.now());
  }

  private Boolean ignoreTokenExpiration(String token) {
    LOGGER.info("Enter JwtTokenUtil.ignoreTokenExpiration() ");
    // here you specify tokens, for that the expiration is ignored
    return false;
  }

  public String generateToken(UserDetails userDetails) {
    LOGGER.info("Enter JwtTokenUtil.generateToken() ");

    Map<String, Object> claims = new HashMap<>();
    return doGenerateToken(claims, userDetails.getUsername());
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {
    LOGGER.info("Enter JwtTokenUtil.doGenerateToken() ");
    final Date createdDate = clock.now();
    final Date expirationDate = calculateExpirationDate(createdDate);
    String generateToken = Jwts.builder().setClaims(claims).setSubject(subject)
        .setIssuedAt(createdDate)
        .setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secret).compact();
    LOGGER.info("Token generated '{}'", generateToken);
    return generateToken;
  }

  public Boolean canTokenBeRefreshed(String token) {
    LOGGER.info("Enter JwtTokenUtil.canTokenBeRefreshed() ");
    return (!isTokenExpired(token) || ignoreTokenExpiration(token));
  }

  public String refreshToken(String token) {
    LOGGER.info("Enter JwtTokenUtil.refreshToken() ");

    final Date createdDate = clock.now();
    final Date expirationDate = calculateExpirationDate(createdDate);

    final Claims claims = getAllClaimsFromToken(token);
    claims.setIssuedAt(createdDate);
    claims.setExpiration(expirationDate);

    return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    LOGGER.info("Enter JwtTokenUtil.validateToken() ");
    JwtUserDetails user = (JwtUserDetails) userDetails;
    final String username = getUsernameFromToken(token);
    return (username.equals(user.getUsername()) && !isTokenExpired(token));
  }

  private Date calculateExpirationDate(Date createdDate) {
    LOGGER.info("Enter JwtTokenUtil.calculateExpirationDate() ");
    return new Date(createdDate.getTime() + expiration * 1000);
  }
}

