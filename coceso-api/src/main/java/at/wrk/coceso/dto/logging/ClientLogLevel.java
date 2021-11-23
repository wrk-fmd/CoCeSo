package at.wrk.coceso.dto.logging;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ClientLogLevel {
    @JsonProperty("error")
    ERROR,

    @JsonProperty("warning")
    WARNING,

    @JsonProperty("info")
    INFO,

    @JsonProperty("debug")
    DEBUG,
}
