package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;
import at.wrk.coceso.alarm.text.sender.AlarmTextSender;
import at.wrk.coceso.alarm.text.service.text.AlarmTextFactory;
import at.wrk.coceso.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class AlarmTextServiceImpl implements AlarmTextService {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTextServiceImpl.class);

    private final AlarmTextFactory alarmTextFactory;
    private final AlarmTextTargetFactory alarmTextTargetFactory;
    private final AlarmTextSender alarmTextSender;
    private final AlarmTextSendingListener alarmTextSendingListener;

    @Autowired
    public AlarmTextServiceImpl(
            final AlarmTextFactory alarmTextFactory,
            final AlarmTextTargetFactory alarmTextTargetFactory,
            final AlarmTextSender alarmTextSender,
            final AlarmTextSendingListener alarmTextSendingListener) {
        this.alarmTextFactory = alarmTextFactory;
        this.alarmTextTargetFactory = alarmTextTargetFactory;
        this.alarmTextSender = alarmTextSender;
        this.alarmTextSendingListener = alarmTextSendingListener;
    }

    @Override
    public Optional<String> createAlarmText(final int incidentId, final AlarmTextType type, final Locale locale) {
        LOG.debug("Creating alarm text for incident #{} of type {}", incidentId, type);
        return alarmTextFactory.createAlarmText(incidentId, type, locale);
    }

    @Override
    public SendAlarmTextResult sendAlarmText(final int incidentId, final String alarmText, final AlarmTextType type, final Locale locale, final User user) {
        List<String> alarmTargets = alarmTextTargetFactory.createTargetList(incidentId, type);
        SendAlarmTextResult result;
        if (alarmTargets.isEmpty()) {
            result = SendAlarmTextResult.NO_TARGETS_FOUND;
        } else {
            LOG.debug("Sending alarm text for incident #{} of type {} to {} targets: {}", incidentId, type, alarmTargets.size(), alarmTargets);
            result = alarmTextSender.sendAlarmText(alarmText, alarmTargets);
            if (result == SendAlarmTextResult.SUCCESS) {
                alarmTextSendingListener.alarmTextSent(incidentId, type, locale, user);
            }
        }

        return result;
    }
}
