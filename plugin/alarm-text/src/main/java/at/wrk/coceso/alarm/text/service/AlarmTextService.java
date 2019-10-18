package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;

import java.util.Locale;
import java.util.Optional;

public interface AlarmTextService {
    Optional<String> createAlarmText(int incidentId, AlarmTextType type, Locale locale);

    SendAlarmTextResult sendAlarmText(int incidentId, String alarmText, AlarmTextType type, final Locale locale);
}
