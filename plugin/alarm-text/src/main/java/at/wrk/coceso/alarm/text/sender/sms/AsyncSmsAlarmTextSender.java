package at.wrk.coceso.alarm.text.sender.sms;

import at.wrk.coceso.alarm.text.configuration.AlarmTextConfiguration;
import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;
import at.wrk.coceso.alarm.text.sender.AlarmTextSender;
import at.wrk.coceso.alarm.text.sender.sms.data.SendTextMessageRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
public class AsyncSmsAlarmTextSender implements AlarmTextSender {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncSmsAlarmTextSender.class);

    private final AsyncRestTemplate asyncRestTemplate;
    private final SmsSendAlarmTextCallback callback;
    private final SendTextMessageRequestFactory sendTextMessageRequestFactory;
    private final URI gatewayUrl;

    @Autowired
    public AsyncSmsAlarmTextSender(
            final AlarmTextConfiguration alarmTextConfiguration,
            final AsyncRestTemplate asyncRestTemplate,
            final SmsSendAlarmTextCallback callback,
            final SendTextMessageRequestFactory sendTextMessageRequestFactory) {
        this.gatewayUrl = alarmTextConfiguration.getSmsGatewayUrl();
        this.asyncRestTemplate = asyncRestTemplate;
        this.callback = callback;
        this.sendTextMessageRequestFactory = sendTextMessageRequestFactory;
    }

    @Override
    public String getSupportedUriSchema() {
        return "tel";
    }

    @Override
    public SendAlarmTextResult sendAlarmText(final String alarmText, final List<String> targets) {
        SendAlarmTextResult result;

        if (gatewayUrl == null) {
            LOG.debug("Incomplete gateway configuration, alarm text cannot be sent because gateway URL is empty.");
            result = SendAlarmTextResult.NO_GATEWAY_CONFIGURED;
        } else {

            Optional<HttpEntity<String>> httpEntity = sendTextMessageRequestFactory.createHttpEntityOfRequest(alarmText, targets);
            if (httpEntity.isPresent()) {
                result = SendAlarmTextResult.SUCCESS;
                ListenableFuture<ResponseEntity<String>> responseFuture = asyncRestTemplate.postForEntity(gatewayUrl, httpEntity.get(), String.class);
                responseFuture.addCallback(callback);
            } else {
                result = SendAlarmTextResult.NO_GATEWAY_CONFIGURED;
            }
        }

        return result;
    }
}
