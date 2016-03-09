package at.wrk.coceso.vienna;

import at.wrk.coceso.service.point.JsonPoi;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ViennaJsonPoi extends JsonPoi {

  @Autowired
  public ViennaJsonPoi(ObjectMapper mapper) throws IOException {
    super(mapper, new ClassPathResource("poi.json"));
  }

}
