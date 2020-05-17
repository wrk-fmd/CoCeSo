package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.logging.ClientLog;
import at.wrk.coceso.dto.logging.ClientLogLevel;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.service.LoggingService;
import at.wrk.coceso.utils.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class LoggingServiceImpl implements LoggingService {

    private static final Logger CLIENT_LOGGER = LoggerFactory.getLogger("at.wrk.coceso.client");
    private static final Logger PATIENT_LOGGER = LoggerFactory.getLogger("at.wrk.coceso.patients");

    @Override
    public void clientLog(final ClientLog clientLog, final String remoteHost) {
        ClientLogLevel level = clientLog.getLogLevel() != null ? clientLog.getLogLevel() : ClientLogLevel.INFO;

        switch (level) {
            case DEBUG:
                CLIENT_LOGGER.debug(buildLogLine(clientLog, remoteHost));
                break;
            case INFO:
                CLIENT_LOGGER.info(buildLogLine(clientLog, remoteHost));
                break;
            case WARNING:
                CLIENT_LOGGER.warn(buildLogLine(clientLog, remoteHost));
                break;
            case ERROR:
                CLIENT_LOGGER.error(buildLogLine(clientLog, remoteHost));
                break;
            default:
                CLIENT_LOGGER.warn("Received unknown log level: {}", level);
                break;
        }
    }

    private String buildLogLine(final ClientLog clientLog, final String remoteHost) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("User '%s' on host '%s' reported:", AuthenticatedUser.getName(), remoteHost));

        if (clientLog.getMessage() != null) {
            stringBuilder.append(String.format(" Message: '%s'.", clientLog.getMessage()));
        }

        if (clientLog.getUrl() != null) {
            stringBuilder.append(String.format(" URL: '%s'.", clientLog.getUrl()));
        }

        if (clientLog.getCodeLine() != null || clientLog.getCodeColumn() != null) {
            stringBuilder.append(String.format("\nLine %d, Column %d", clientLog.getCodeLine(), clientLog.getCodeColumn()));
        }

        if (clientLog.getStack() != null) {
            stringBuilder.append(String.format("\nStacktrace:\n%s", clientLog.getStack()));
        }

        return stringBuilder.toString();
    }

    @Override
    public void logPatientAccess(final Patient patient) {
        PATIENT_LOGGER.info("{}: Reading patient information of patient '{}'", AuthenticatedUser.getName(), patient);
    }

    @Override
    public void logPatientAccess(final Collection<Patient> infos, final Concern concern) {
        PATIENT_LOGGER.info("{}: Loaded patients for concern '{}'. {} patients matched.",
                AuthenticatedUser.getName(), concern, infos == null ? -1 : infos.size());
    }

    @Override
    public void logPatientAccess(final Collection<Patient> infos, final Concern concern, final String query) {
        PATIENT_LOGGER.info("{}: Searching patients for query '{}' in concern '{}'. {} patients matched.",
                AuthenticatedUser.getName(), query, concern, infos == null ? -1 : infos.size());
    }
}
