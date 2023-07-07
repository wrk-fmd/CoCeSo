package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.IncidentWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Locale;

@Component
public class AlarmTextConfirmationListener implements AlarmTextSendingListener {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTextConfirmationListener.class);

    private final MessageSource messageSource;
    private final IncidentService incidentService;
    private final IncidentWriteService incidentWriteService;

    @Autowired
    public AlarmTextConfirmationListener(
            final MessageSource messageSource,
            final IncidentService incidentService,
            final IncidentWriteService incidentWriteService) {
        this.messageSource = messageSource;
        this.incidentService = incidentService;
        this.incidentWriteService = incidentWriteService;
    }

    @Override
    @Transactional
    public void alarmTextSent(final int incidentId, final AlarmTextType type, final Locale locale) {
        String confirmationMessage;
        if (type == AlarmTextType.INCIDENT_INFORMATION) {
            confirmationMessage = getConfirmationMessage("incident.alarm.sent.info", locale);
        } else {
            confirmationMessage = getConfirmationMessage("incident.alarm.sent.casusnumber", locale);
        }

        Incident incident = incidentService.getById(incidentId);

        if (incident != null) {
            String infoString = incident.getInfo();
            incident.setInfo(infoString + "\n" + confirmationMessage);
            incidentWriteService.update(incident, incident.getConcern());
        } else {
            LOG.warn("Failed to write sent-alarm confirmation to incident. Incident does not exist. incidentId={}", incidentId);
        }
    }

    private String getConfirmationMessage(final String messageCode, final Locale locale) {
        String timestamp = buildTime();
        String defaultMessage = "Alarm message sent at " + timestamp;
        return messageSource.getMessage(messageCode, new Object[] {timestamp}, defaultMessage, locale);
    }

    private String buildTime() {
        LocalTime localTime = LocalTime.now();
        return String.format("%02d:%02d:%02d", localTime.getHour(), localTime.getMinute(), localTime.getSecond());
    }
}
