package at.wrk.coceso.controller.handler;

import at.wrk.coceso.contract.client.ClientLog;
import at.wrk.coceso.contract.client.ClientLogLevel;
import at.wrk.coceso.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientLogger {
    private static final Logger LOG = LoggerFactory.getLogger(ClientLogger.class);

    public void handleClientLog(final ClientLog clientLog, final User user, final String remoteHost) {
        ClientLogLevel level = Optional.ofNullable(clientLog.getLogLevel()).orElse(ClientLogLevel.INFO);

        switch (level) {
            case DEBUG:
                if (LOG.isDebugEnabled()) LOG.debug(buildLogLine(clientLog, user, remoteHost));
                break;
            case INFO:
                if (LOG.isInfoEnabled()) LOG.info(buildLogLine(clientLog, user, remoteHost));
                break;
            case WARNING:
                if (LOG.isWarnEnabled()) LOG.warn(buildLogLine(clientLog, user, remoteHost));
                break;
            case ERROR:
                if (LOG.isErrorEnabled()) LOG.error(buildLogLine(clientLog, user, remoteHost));
                break;
            default:
                LOG.warn("Received unknown log level: {}", level);
                break;
        }
    }

    private String buildLogLine(final ClientLog clientLog, final User user, final String remoteHost) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("User '");
        stringBuilder.append(user.getUsername());
        stringBuilder.append("' on remote host '");
        stringBuilder.append(remoteHost);
        stringBuilder.append("' reported:");

        if (clientLog.getMessage() != null) {
            stringBuilder.append(" ");
            stringBuilder.append(clientLog.getMessage());
        }

        if (clientLog.getUrl() != null) {
            stringBuilder.append(" URL: '");
            stringBuilder.append(clientLog.getUrl());
            stringBuilder.append("'.");
        }

        if (clientLog.getCodeLine() != null || clientLog.getCodeColumn() != null) {
            stringBuilder.append("\nLine ");
            stringBuilder.append(clientLog.getCodeLine());
            stringBuilder.append(", Column ");
            stringBuilder.append(clientLog.getCodeColumn());
        }

        if (clientLog.getStack() != null) {
            stringBuilder.append("\nStacktrace:\n");
            stringBuilder.append(clientLog.getStack());
        }

        return stringBuilder.toString();
    }
}
