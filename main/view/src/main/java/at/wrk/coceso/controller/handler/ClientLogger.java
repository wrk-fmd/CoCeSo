package at.wrk.coceso.controller.handler;

import at.wrk.coceso.contract.client.ClientLog;
import at.wrk.coceso.contract.client.ClientLogLevel;
import at.wrk.coceso.utils.AuthenicatedUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientLogger {
    private static final Logger LOG = LoggerFactory.getLogger(ClientLogger.class);

    private final AuthenicatedUserProvider authenicatedUserProvider;

    @Autowired
    public ClientLogger(final AuthenicatedUserProvider authenicatedUserProvider) {
        this.authenicatedUserProvider = authenicatedUserProvider;
    }

    public void handleClientLog(final ClientLog clientLog, final String remoteHost) {
        ClientLogLevel level = Optional.ofNullable(clientLog.getLogLevel()).orElse(ClientLogLevel.INFO);

        switch (level) {
            case DEBUG:
                if (LOG.isDebugEnabled()) LOG.debug(buildLogLine(clientLog, remoteHost));
                break;
            case INFO:
                if (LOG.isInfoEnabled()) LOG.info(buildLogLine(clientLog, remoteHost));
                break;
            case WARNING:
                if (LOG.isWarnEnabled()) LOG.warn(buildLogLine(clientLog, remoteHost));
                break;
            case ERROR:
                if (LOG.isErrorEnabled()) LOG.error(buildLogLine(clientLog, remoteHost));
                break;
            default:
                LOG.warn("Received unknown log level: {}", level);
                break;
        }
    }

    private String buildLogLine(final ClientLog clientLog, final String remoteHost) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("User '");
        stringBuilder.append(authenicatedUserProvider.getAuthenticatedUser());
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
