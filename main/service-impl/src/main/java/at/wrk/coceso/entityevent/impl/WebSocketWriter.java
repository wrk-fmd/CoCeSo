package at.wrk.coceso.entityevent.impl;

import at.wrk.coceso.entity.helper.SequencedDeleted;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

class WebSocketWriter<T> implements EntityEventListener<T> {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketWriter.class);

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
        LOG.trace("Got changed entity: {}", entity);

        if (delete != null) {
            Integer deleteId = delete.apply(entity);
            if (deleteId != null) {
                entityDeleted(deleteId, concern, hver, seq);
                return;
            }
        }

        String formattedUrl = String.format(url, concern);
        LOG.debug("Publishing changed entity on WebSocket. URL={}, Entity={}", formattedUrl, entity);
        messagingTemplate.send(formattedUrl, new SequencedResponse<>(hver, seq, entity), jsonView);
    }

    @Override
    public void entityDeleted(int id, int concern, int hver, int seq) {
        String formattedUrl = String.format(url, concern);
        LOG.debug("Publishing deleted entity on WebSocket. URL={}, id={}", formattedUrl, id);
        messagingTemplate.send(formattedUrl, new SequencedDeleted(hver, seq, id), null);
    }

    @Override
    public boolean isSupported(final Class<?> supportedClass) {
        // This listener is registered dynamically for the correct type. No type check needed.
        return true;
    }
}
