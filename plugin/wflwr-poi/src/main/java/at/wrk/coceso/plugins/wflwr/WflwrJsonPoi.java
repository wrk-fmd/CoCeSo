package at.wrk.coceso.plugins.wflwr;

import at.wrk.geocode.poi.JsonPoi;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Order(12)
public class WflwrJsonPoi extends JsonPoi {

  @Autowired
  public WflwrJsonPoi(ObjectMapper mapper) throws IOException {
    super(mapper, new ClassPathResource("wflwr.json"));
  }

}
