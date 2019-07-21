package at.wrk.coceso.contract.client;

import at.wrk.coceso.contract.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * JS logging functionality
 */
public class ClientLog {

    private final String message;
    private final String url;
    private final Integer codeLine;
    private final Integer codeColumn;
    private final String stack;
    private final ClientLogLevel logLevel;

    @JsonCreator
    public ClientLog(
            @JsonProperty("message") final String message,
            @JsonProperty("url") final String url,
            @JsonProperty("codeLine") final Integer codeLine,
            @JsonProperty("codeColumn") final Integer codeColumn,
            @JsonProperty("stack") final String stack,
            @JsonProperty("logLevel") final ClientLogLevel logLevel) {
        this.message = message;
        this.url = url;
        this.codeLine = codeLine;
        this.codeColumn = codeColumn;
        this.stack = stack;
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public Integer getCodeLine() {
        return codeLine;
    }

    public Integer getCodeColumn() {
        return codeColumn;
    }

    public String getStack() {
        return stack;
    }

    public ClientLogLevel getLogLevel() {
        return logLevel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.STYLE)
                .append("message", message)
                .append("url", url)
                .append("codeLine", codeLine)
                .append("codeColumn", codeColumn)
                .append("stack", stack)
                .append("logLevel", logLevel)
                .toString();
    }
}
