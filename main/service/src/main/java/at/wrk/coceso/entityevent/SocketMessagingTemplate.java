package at.wrk.coceso.entityevent;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SocketMessagingTemplate {

  private final SimpMessagingTemplate template;

  public SocketMessagingTemplate() {
    this.template = null;
  }

  @Autowired(required = false)
  public SocketMessagingTemplate(SimpMessagingTemplate template) {
    this.template = template;
    //this.template = new ConversionMessagingTemplate(template.getMessageChannel());
  }

  public void send(String destination, Object payload, Class<?> jsonView) {
    if (template != null) {
      if (jsonView == null) {
        template.convertAndSend(destination, payload);
      } else {
        template.convertAndSend(destination, payload, Collections.singletonMap(SimpMessagingTemplate.CONVERSION_HINT_HEADER, jsonView));
      }
    }
  }

  private static class ConversionMessagingTemplate extends SimpMessagingTemplate {

    public ConversionMessagingTemplate(MessageChannel messageChannel) {
      super(messageChannel);
    }

    @Override
    protected Map<String, Object> processHeadersToSend(Map<String, Object> headers) {
      if (headers != null) {
        headers = headers.entrySet().stream()
            .filter(e -> CONVERSION_HINT_HEADER.equals(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      }
      return super.processHeadersToSend(headers);
    }
  }

}
