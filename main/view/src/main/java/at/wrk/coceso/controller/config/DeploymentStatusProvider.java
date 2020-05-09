package at.wrk.coceso.controller.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeploymentStatusProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentStatusProvider.class);

    private static final String ALARM_TEXT_CLASS = "at.wrk.coceso.alarm.text.controller.AlarmTextController";
    private static final String GEOBROKER_CONTROLLER_CLASS = "at.wrk.coceso.plugin.geobroker.controller.GeobrokerUnitController";

    private final boolean alarmTextModuleDeployed;
    private final boolean geoBrokerModuleDeployed;

    public DeploymentStatusProvider() {
        this.alarmTextModuleDeployed = checkIfClassIsPresent(ALARM_TEXT_CLASS);
        this.geoBrokerModuleDeployed = checkIfClassIsPresent(GEOBROKER_CONTROLLER_CLASS);
    }

    public boolean isAlarmTextModuleDeployed() {
        return alarmTextModuleDeployed;
    }

    public boolean isGeoBrokerModuleDeployed() {
        return geoBrokerModuleDeployed;
    }

    private boolean checkIfClassIsPresent(final String className) {
        boolean alarmTextClassPresent;
        try {
            Class.forName(className);
            LOG.info("Class was found during deployment status check: '{}'. Associated feature is ENABLED.", className);
            alarmTextClassPresent = true;
        } catch (ClassNotFoundException e) {
            LOG.info("Class was not found during deployment status check: '{}'. Associated feature is DISABLED. Message: {}", className, e.getMessage());
            alarmTextClassPresent = false;
        }
        return alarmTextClassPresent;
    }
}
