package at.wrk.coceso.radio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomingMessageDto {

    private String channel;
    private String channelName;
    private String sender;
    private Instant timestamp;
    private boolean emergency;
    private boolean outgoingTalkburst;

    /**
     * Unique identifier of the channel. Might be the technical connection to an analog channel, or the group identifier of a digital talkgroup.
     */
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * User-readable name of the channel.
     */
    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(final String channelName) {
        this.channelName = channelName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public boolean isOutgoingTalkburst() {
        return outgoingTalkburst;
    }

    public void setOutgoingTalkburst(final boolean outgoingTalkburst) {
        this.outgoingTalkburst = outgoingTalkburst;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("channel", channel)
                .append("channelName", channelName)
                .append("sender", sender)
                .append("timestamp", timestamp)
                .append("emergency", emergency)
                .append("outgoingTalkburst", outgoingTalkburst)
                .toString();
    }
}
