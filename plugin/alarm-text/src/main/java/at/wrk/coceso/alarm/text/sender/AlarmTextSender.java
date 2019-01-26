package at.wrk.coceso.alarm.text.sender;

import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;

import java.util.List;

public interface AlarmTextSender {
    String getSupportedUriSchema();

    SendAlarmTextResult sendAlarmText(String alarmText, List<String> targets);
}
