package at.wrk.coceso.alarm.text.sender.tetra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class TetraSendAlarmTextCallback implements ListenableFutureCallback<ResponseEntity<String>> {
    private static final Logger LOG = LoggerFactory.getLogger(TetraSendAlarmTextCallback.class);

    @Override
    public void onFailure(final Throwable throwable) {
        LOG.warn("Failed to send alarm text to TETRA gateway. Message: {}", throwable.getMessage());
        LOG.debug("Underlying exception", throwable);
    }

    @Override
    public void onSuccess(final ResponseEntity<String> responseBody) {
        LOG.debug("Successfully written alarm text to TETRA gateway.");
        LOG.trace("TETRA Gateway returned: {}", responseBody);
    }
}
