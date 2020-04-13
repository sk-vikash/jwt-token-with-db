package in.rdf.rest.webservices.CityResource;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CityResource {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(CityResource.class);


  @Autowired
  private CityJpaRepository cityJpaRepository;

  @GetMapping("/jpa/get-all-city")
  public List<City> getAllCityDetails() {
    LOGGER.info("Enter CityResource.getAllCityDetails() ");
    //generatePassword(); //Use this if you need to generate a new password.
    return cityJpaRepository.findAll();
  }

  @GetMapping("/jpa/get-city-within-state/{state}")
  public List<City> findByState(@PathVariable String state) {
    LOGGER.info("Enter CityResource.findByState() ");
    return cityJpaRepository.findByState(state);
  }

  @GetMapping("/jpa/get-city/{id}")
  public City getCityDetailsById(@PathVariable long id) {
    LOGGER.info("Enter CityResource.getCityDetailsById() ");
    return cityJpaRepository.findById(id).get();
  }

  private void generatePassword(){
    String password = "Password$@!54321";
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    String encode = bCryptPasswordEncoder.encode(password);
    LOGGER.info("encode = " + encode);
  }
}