package at.wrk.coceso.radio.api.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.Instant;

public class ReceivedCallDto implements Serializable, Comparable<ReceivedCallDto> {

    private String port;

    private String ani;

    private boolean emergency;

    private Instant timestamp;

    public ReceivedCallDto() {
    }

    public ReceivedCallDto(String port, String ani, boolean emergency) {
        this.port = port;
        this.ani = ani;
        this.emergency = emergency;
        this.timestamp = Instant.now();
    }

    @Override
    public int compareTo(ReceivedCallDto that) {
        return this.timestamp.compareTo(that.timestamp);
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAni() {
        return ani;
    }

    public void setAni(String ani) {
        this.ani = ani;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("port", port)
                .append("ani", ani)
                .append("emergency", emergency)
                .append("timestamp", timestamp)
                .toString();
    }
}
