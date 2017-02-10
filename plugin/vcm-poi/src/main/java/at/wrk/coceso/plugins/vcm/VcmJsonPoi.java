package at.wrk.coceso.plugins.vcm;

import at.wrk.geocode.poi.JsonPoi;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Order(11)
public class VcmJsonPoi extends JsonPoi {

  @Autowired
  public VcmJsonPoi(ObjectMapper mapper) throws IOException {
    super(mapper, new ClassPathResource("vcm.json"));
  }

}
