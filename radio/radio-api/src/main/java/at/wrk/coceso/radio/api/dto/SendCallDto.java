package at.wrk.coceso.radio.api.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SendCallDto implements Serializable {

    private String port;

    @NotNull
    @Pattern(regexp = "^\\d{7}$")
    private String ani;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = StringUtils.trimToNull(port);
    }

    public String getAni() {
        return ani;
    }

    public void setAni(String ani) {
        this.ani = StringUtils.trimToNull(ani);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("port", port)
                .append("ani", ani)
                .toString();
    }
}
