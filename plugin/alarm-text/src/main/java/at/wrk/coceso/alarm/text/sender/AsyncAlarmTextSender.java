package at.wrk.coceso.alarm.text.sender;

import at.wrk.coceso.alarm.text.configuration.AlarmTextConfiguration;
import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;
import at.wrk.coceso.alarm.text.sender.api.SendTextMessageRequest;
import at.wrk.coceso.alarm.text.util.UriUtil;
import at.wrk.coceso.utils.HttpEntities;
import com.google.gson.Gson;
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

@Component
public class AsyncAlarmTextSender implements AlarmTextSender {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncAlarmTextSender.class);

    private final Gson gson;
    private final AsyncRestTemplate asyncRestTemplate;
    private final SendAlarmTextCallback callback;
    private final URI gatewayUrl;
    private final String authenticationToken;

    @Autowired
    public AsyncAlarmTextSender(
            final AlarmTextConfiguration alarmTextConfiguration,
            final Gson gson,
            final AsyncRestTemplate asyncRestTemplate,
            final SendAlarmTextCallback callback) {
        this.gatewayUrl = buildSendUrl(alarmTextConfiguration.getAlarmTextGatewayUrl());
        this.authenticationToken = alarmTextConfiguration.getAuthenticationToken();
        this.gson = gson;
        this.asyncRestTemplate = asyncRestTemplate;
        this.callback = callback;
    }

    private static URI buildSendUrl(final URI alarmTextGatewayUrl) {
        return alarmTextGatewayUrl == null ? null : UriUtil.appendPath(alarmTextGatewayUrl, "/sms/send");
    }

    @Override
    public SendAlarmTextResult sendAlarmText(final String alarmText, final List<String> targets) {
        SendAlarmTextResult result;

        if (gatewayUrl == null || authenticationToken == null) {
            LOG.debug("Incomplete gateway configuration, alarm text cannot be sent. gatewayUrl={}, authenticationToken={}", gatewayUrl, authenticationToken);
            result = SendAlarmTextResult.NO_GATEWAY_CONFIGURED;
        } else {
            result = SendAlarmTextResult.SUCCESS;

            SendTextMessageRequest request = new SendTextMessageRequest(authenticationToken, targets, alarmText);
            HttpEntity<String> httpEntity = serializeRequest(request);
            ListenableFuture<ResponseEntity<String>> responseFuture = asyncRestTemplate.postForEntity(gatewayUrl, httpEntity, String.class);
            responseFuture.addCallback(callback);
        }

        return result;
    }

    private HttpEntity<String> serializeRequest(final SendTextMessageRequest request) {
        String jsonRequest = gson.toJson(request);
        return HttpEntities.createHttpEntityForJsonString(jsonRequest);
    }
}
