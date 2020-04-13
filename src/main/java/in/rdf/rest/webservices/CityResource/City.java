package in.rdf.rest.webservices.CityResource;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class City {
  @Id @GeneratedValue
  private Long id;
  private String city;
  private String state;
  private String country;

  public City() {

  }

  public City(String city, String state, String country) {
    this.city = city;
    this.state = state;
    this.country = country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}