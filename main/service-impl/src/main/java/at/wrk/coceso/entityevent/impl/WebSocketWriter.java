package at.wrk.coceso.entityevent.impl;

import at.wrk.coceso.entity.helper.SequencedDeleted;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventListener;
import java.util.function.Function;
import org.slf4j.LoggerFactory;

class WebSocketWriter<T> implements EntityEventListener<T> {

  private final SocketMessagingTemplate messagingTemplate;
  private final String url;
  private final Class<?> jsonView;
  private final Function<T, Integer> delete;

  public WebSocketWriter(SocketMessagingTemplate messagingTemplate, String url, Class<?> jsonView, Function<T, Integer> delete) {
    this.messagingTemplate = messagingTemplate;
    this.url = url;
    this.jsonView = jsonView;
    this.delete = delete;
  }

  @Override
  public void entityChanged(T entity, int concern, int hver, int seq) {
    LoggerFactory.getLogger(WebSocketWriter.class).debug("{}: {}", url, entity);
    if (delete != null) {
      Integer deleteId = delete.apply(entity);
      if (deleteId != null) {
        entityDeleted(deleteId, concern, hver, seq);
        return;
      }
    }

    messagingTemplate.send(String.format(url, concern), new SequencedResponse<>(hver, seq, entity), jsonView);
  }

  @Override
  public void entityDeleted(int id, int concern, int hver, int seq) {
    messagingTemplate.send(String.format(url, concern), new SequencedDeleted(hver, seq, id), null);
  }

}
