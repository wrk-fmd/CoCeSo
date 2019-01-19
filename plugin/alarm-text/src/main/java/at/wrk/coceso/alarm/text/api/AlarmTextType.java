package at.wrk.coceso.alarm.text.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AlarmTextType {
    @JsonProperty("incidentInformation")
    INCIDENT_INFORMATION,

    @JsonProperty("casusnumberBooking")
    CASUSNUMBER_BOOKING,
}
