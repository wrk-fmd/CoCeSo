package at.wrk.coceso.alarm.text.sender.sms.data;

import at.wrk.coceso.alarm.text.configuration.AlarmTextConfiguration;
import at.wrk.coceso.utils.HttpEntities;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SendTextMessageRequestFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SendTextMessageRequestFactory.class);

    private final static Map<String, SmsGatewayType> TYPE_MAPPING = ImmutableMap.of(
            AlarmTextConfiguration.DEFAULT_SMS_GATEWAY_TYPE, SmsGatewayType.GAMMU,
            "internal", SmsGatewayType.INTERNAL
    );

    private final String authenticationToken;
    private final SmsGatewayType gatewayType;
    private final Gson gson;

    @Autowired
    public SendTextMessageRequestFactory(
            final AlarmTextConfiguration alarmTextConfiguration,
            final Gson gson) {
        this.authenticationToken = alarmTextConfiguration.getAuthenticationToken();
        gatewayType = Optional.ofNullable(alarmTextConfiguration.getSmsGatewayType())
                .map(TYPE_MAPPING::get)
                .orElse(SmsGatewayType.GAMMU);
        this.gson = gson;
    }

    public Optional<HttpEntity<String>> createHttpEntityOfRequest(final String alarmText, final List<String> targets) {
        Optional<HttpEntity<String>> httpEntity = Optional.empty();

        if (authenticationToken != null) {
            SendTextMessageRequest request = createRequest(alarmText, targets);

            String jsonRequest = gson.toJson(request);
            if (gatewayType == SmsGatewayType.INTERNAL) {
                httpEntity = Optional.of(HttpEntities.createHttpEntityForJsonString(jsonRequest, authenticationToken));
            } else {
                httpEntity = Optional.of(HttpEntities.createHttpEntityForJsonString(jsonRequest));

            }
        } else {
            LOG.info("Incomplete SMS gateway configuration. authentication token is missing.");
        }

        return httpEntity;
    }

    private SendTextMessageRequest createRequest(final String alarmText, final List<String> targets) {
        SendTextMessageRequest request;

        switch (gatewayType) {
            case INTERNAL:
                request = createInternalRequest(alarmText, targets);
                break;
            case GAMMU:
            default:
                request = createGammuRequest(alarmText, targets);
                break;
        }

        return request;
    }

    private GammuSendTextMessageRequest createGammuRequest(final String alarmText, final List<String> targets) {
        return new GammuSendTextMessageRequest(authenticationToken, targets, alarmText);
    }

    private NiuSendTextMessageRequest createInternalRequest(final String alarmText, final List<String> targets) {
        return new NiuSendTextMessageRequest(targets, alarmText);
    }
}
