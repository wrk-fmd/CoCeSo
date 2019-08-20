package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;
import at.wrk.coceso.alarm.text.sender.AlarmTextSender;
import at.wrk.coceso.alarm.text.service.text.AlarmTextFactory;
import at.wrk.coceso.entity.User;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class AlarmTextServiceImpl implements AlarmTextService {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTextServiceImpl.class);

    private final AlarmTextFactory alarmTextFactory;
    private final AlarmTextTargetFactory alarmTextTargetFactory;
    private final Map<String, AlarmTextSender> alarmTextSenders;
    private final AlarmTextSendingListener alarmTextSendingListener;

    @Autowired
    public AlarmTextServiceImpl(
            final AlarmTextFactory alarmTextFactory,
            final AlarmTextTargetFactory alarmTextTargetFactory,
            final List<AlarmTextSender> alarmTextSenderList,
            final AlarmTextSendingListener alarmTextSendingListener) {
        this.alarmTextFactory = alarmTextFactory;
        this.alarmTextTargetFactory = alarmTextTargetFactory;
        this.alarmTextSenders = alarmTextSenderList.stream().collect(toMap(AlarmTextSender::getSupportedUriSchema, Function.identity()));
        this.alarmTextSendingListener = alarmTextSendingListener;
    }

    @Override
    public Optional<String> createAlarmText(final int incidentId, final AlarmTextType type, final Locale locale) {
        LOG.debug("Creating alarm text for incident #{} of type {}", incidentId, type);
        return alarmTextFactory.createAlarmText(incidentId, type, locale);
    }

    @Override
    public SendAlarmTextResult sendAlarmText(
            final int incidentId,
            final String alarmText,
            final AlarmTextType alarmType,
            final Locale locale) {
        Map<String, List<String>> alarmTargets = alarmTextTargetFactory.createTargetList(incidentId, alarmType);

        String sanitizedAlarmText = sanitizeString(alarmText);

        Map<String, SendAlarmTextResult> resultMap = alarmAllTargets(incidentId, sanitizedAlarmText, alarmType, alarmTargets);
        LOG.debug("Result of alarm text send operation: {}", resultMap);

        SendAlarmTextResult overallResult = calculateOverallResult(resultMap);

        if (overallResult == SendAlarmTextResult.SUCCESS) {
            alarmTextSendingListener.alarmTextSent(incidentId, alarmType, locale);
        }

        return overallResult;
    }

    private String sanitizeString(final String alarmText) {
        return alarmText.replace("\r\n", "\n").replace("\r", "\n");
    }

    private Map<String, SendAlarmTextResult> alarmAllTargets(
            final int incidentId,
            final String alarmText,
            final AlarmTextType alarmType,
            final Map<String, List<String>> alarmTargets) {
        final Map<String, SendAlarmTextResult> resultMap = Maps.newHashMap();

        alarmTargets.forEach((targetType, targetList) -> {
            if (targetList.isEmpty()) {
                resultMap.put(targetType, SendAlarmTextResult.NO_TARGETS_FOUND);
            } else {
                LOG.debug("Sending alarm text {} for incident #{} to {} targets of type {}. Targets: {}", alarmType, incidentId, alarmTargets.size(), targetType, alarmTargets);
                AlarmTextSender sender = this.alarmTextSenders.get(targetType);
                if (sender != null) {
                    SendAlarmTextResult result = sender.sendAlarmText(alarmText, targetList);
                    resultMap.put(targetType, result);
                } else {
                    LOG.warn("Could not load sender for type {}", targetType);
                    resultMap.put(targetType, SendAlarmTextResult.NO_GATEWAY_CONFIGURED);
                }
            }
        });
        return resultMap;
    }

    private SendAlarmTextResult calculateOverallResult(final Map<String, SendAlarmTextResult> resultMap) {
        SendAlarmTextResult overallResult;
        if (resultMap.isEmpty()) {
            overallResult = SendAlarmTextResult.NO_TARGETS_FOUND;
        } else if (resultMap.containsValue(SendAlarmTextResult.SUCCESS)) {
            overallResult = SendAlarmTextResult.SUCCESS;
        } else {
            overallResult = resultMap.containsValue(SendAlarmTextResult.NO_TARGETS_FOUND) ? SendAlarmTextResult.NO_TARGETS_FOUND : SendAlarmTextResult.NO_GATEWAY_CONFIGURED;
        }

        return overallResult;
    }
}
