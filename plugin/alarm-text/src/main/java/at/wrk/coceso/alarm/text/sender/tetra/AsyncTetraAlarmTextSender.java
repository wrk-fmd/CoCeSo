package at.wrk.coceso.alarm.text.sender.tetra;

import at.wrk.coceso.alarm.text.configuration.AlarmTextConfiguration;
import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;
import at.wrk.coceso.alarm.text.sender.AlarmTextSender;
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
public class AsyncTetraAlarmTextSender implements AlarmTextSender {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncTetraAlarmTextSender.class);

    private final Gson gson;
    private final AsyncRestTemplate asyncRestTemplate;
    private final TetraSendAlarmTextCallback callback;
    private final URI gatewayUrl;
    private final String authenticationToken;

    @Autowired
    public AsyncTetraAlarmTextSender(
            final AlarmTextConfiguration alarmTextConfiguration,
            final Gson gson,
            final AsyncRestTemplate asyncRestTemplate,
            final TetraSendAlarmTextCallback callback) {
        this.gson = gson;
        this.asyncRestTemplate = asyncRestTemplate;
        this.callback = callback;
        this.gatewayUrl = buildSendUrl(alarmTextConfiguration.getTetraGatewayUri());
        this.authenticationToken = alarmTextConfiguration.getTetraAuthenticationToken();
    }

    private static URI buildSendUrl(final URI alarmTextGatewayUrl) {
        return alarmTextGatewayUrl == null ? null : UriUtil.appendPath(alarmTextGatewayUrl, "/sds/send");
    }

    @Override
    public String getSupportedUriSchema() {
        return "tetra";
    }

    @Override
    public SendAlarmTextResult sendAlarmText(final String alarmText, final List<String> targets) {
        SendAlarmTextResult result;

        if (gatewayUrl == null) {
            LOG.debug("No TETRA gateway configured. Cannot send SDS to mobiles '{}'.", targets);
            result = SendAlarmTextResult.NO_GATEWAY_CONFIGURED;
        } else {
            result = SendAlarmTextResult.SUCCESS;

            targets.forEach(targetIssi -> sendSds(alarmText, targetIssi));
        }

        return result;
    }

    private void sendSds(final String alarmText, final String targetIssi) {
        SendSdsRequest request = new SendSdsRequest(targetIssi, alarmText, OutgoingSdsType.INDIVIDUAL_ACK);
        HttpEntity<String> httpEntity = serializeRequest(request);
        LOG.debug("Sending alarm text to target '{}'.", targetIssi);
        ListenableFuture<ResponseEntity<String>> responseFuture = asyncRestTemplate.postForEntity(gatewayUrl, httpEntity, String.class);
        responseFuture.addCallback(callback);
    }

    private HttpEntity<String> serializeRequest(final SendSdsRequest request) {
        String jsonRequest = gson.toJson(request);
        HttpEntity<String> httpEntity;
        if (authenticationToken != null) {
            httpEntity = HttpEntities.createHttpEntityForJsonStringWithBearerTokenAuthentication(jsonRequest, authenticationToken);
        } else {
            httpEntity = HttpEntities.createHttpEntityForJsonString(jsonRequest);
        }

        return httpEntity;
    }
}
