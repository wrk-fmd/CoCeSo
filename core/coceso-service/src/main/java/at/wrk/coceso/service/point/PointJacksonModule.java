package at.wrk.coceso.service.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointJacksonModule extends SimpleModule {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  public PointJacksonModule() {
    super();
    setSerializerModifier(new PointSerializerModifier());
  }

  @PostConstruct
  public void postConstruct() {
    objectMapper.registerModule(this);
  }
}
