package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.poi.JsonPoi;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class ViennaJsonPoi extends JsonPoi {

  @Autowired
  public ViennaJsonPoi(ObjectMapper mapper) throws IOException {
    super(mapper, new ClassPathResource("ehs.json"), new ClassPathResource("hospitals.json"), new ClassPathResource("wrk.json"));
  }

}
