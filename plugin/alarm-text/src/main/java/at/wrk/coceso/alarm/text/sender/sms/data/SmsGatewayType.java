package at.wrk.coceso.alarm.text.sender.sms.data;

public enum SmsGatewayType {
    /**
     * Java Gammu Head compatible. See <a href="https://github.com/robo-w/java-gammu-head">Gammu Head Project</a>.
     */
    GAMMU,

    /**
     * Internal SMS Gateway type "NIU" with Basic authentication.
     */
    INTERNAL,

    /**
     * Internal SMS Gateway type with Bearer Token authentication.
     */
    INTERNAL_BEARER_TOKEN,
}
