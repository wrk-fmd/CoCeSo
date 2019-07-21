package at.wrk.coceso.controller.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeploymentStatusProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentStatusProvider.class);

    private final boolean alarmTextModuleDeployed;

    public DeploymentStatusProvider() {
        boolean alarmTextClassPresent;
        try {
            Class.forName("at.wrk.coceso.alarm.text.controller.AlarmTextController");
            LOG.debug("Alarm Text Module is present in classpath.");
            alarmTextClassPresent = true;
        } catch (ClassNotFoundException e) {
            alarmTextClassPresent = false;
        }

        this.alarmTextModuleDeployed = alarmTextClassPresent;
    }

    public boolean isAlarmTextModuleDeployed() {
        return alarmTextModuleDeployed;
    }
}
