package in.rdf.rest.webservices.TokenUtil.JwtTokenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtInMemoryUserDetailsService implements UserDetailsService {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(JwtInMemoryUserDetailsService.class);

  static List<JwtUserDetails> inMemoryUserList = new ArrayList<>();

  static {
    LOGGER.info("Enter JwtInMemoryUserDetailsService class static block");
    inMemoryUserList.add(new JwtUserDetails(1L, "jamshedpur",
        "$2a$10$QWoLbTli39vbqtQlNe25Su.nnDl.s2iLKwa9azw.hqjgFJpt9GS3O", "ROLE_USER_2"));
    LOGGER.info("Exit JwtInMemoryUserDetailsService class static block");
  }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LOGGER.info(
        "Enter JwtInMemoryUserDetailsService.loadUserByUsername(String username) " + username);

    Optional<JwtUserDetails> findFirst = inMemoryUserList.stream()
        .filter(user -> user.getUsername().equals(username)).findFirst();

    LOGGER.info(
        "Exit JwtInMemoryUserDetailsService.loadUserByUsername(String username) ");

    if (!findFirst.isPresent()) {
      throw new UsernameNotFoundException(String.format("USER_NOT_FOUND '%s'.", username));
    }

    return findFirst.get();
  }

}


