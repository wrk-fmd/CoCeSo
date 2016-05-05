package at.wrk.coceso.vienna;

import at.wrk.coceso.service.point.JsonPoi;
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
    super(mapper,
        new ClassPathResource("ehs.json"),
//        new ClassPathResource("vcm.json"),
        new ClassPathResource("hospitals.json"), new ClassPathResource("wrk.json"),
        new ClassPathResource("wflwr.json"));
  }

}
