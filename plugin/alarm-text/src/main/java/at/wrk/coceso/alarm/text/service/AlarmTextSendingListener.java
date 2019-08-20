package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;

import java.util.Locale;

public interface AlarmTextSendingListener {
    void alarmTextSent(int incidentId, AlarmTextType type, Locale locale);
}
