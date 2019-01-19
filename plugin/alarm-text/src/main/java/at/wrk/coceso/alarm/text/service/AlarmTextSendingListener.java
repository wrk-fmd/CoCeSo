package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.entity.User;

import java.util.Locale;

public interface AlarmTextSendingListener {
    void alarmTextSent(int incidentId, AlarmTextType type, Locale locale, User user);
}
