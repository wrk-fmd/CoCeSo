package at.wrk.coceso.dto.logging;

import lombok.Getter;
import lombok.Setter;

/**
 * JS logging functionality
 */
@Getter
@Setter
public class ClientLog {

    private String message;
    private String url;
    private Integer codeLine;
    private Integer codeColumn;
    private String stack;
    private ClientLogLevel logLevel;

    public void setMessage(String message) {
        this.message = message != null ? message.trim() : null;
    }

    public void setUrl(String url) {
        this.url = url != null ? url.trim() : null;
    }

    public void setStack(String stack) {
        this.stack = stack != null ? stack.trim() : null;
    }
}
