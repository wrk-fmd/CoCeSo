package at.wrk.coceso.alarm.text.sender.sms.data;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Uses Java Gammu Head. See https://github.com/robo-w/java-gammu-head#json-api.
 */
public class GammuSendTextMessageRequest implements SendTextMessageRequest {
    private static final long serialVersionUID = 1L;

    private final String authenticationToken;
    private final List<String> targetPhoneNumbers;
    private final String messageContent;

    public GammuSendTextMessageRequest(final String authenticationToken, final List<String> targetPhoneNumbers, final String messageContent) {
        this.authenticationToken = authenticationToken;
        this.targetPhoneNumbers = targetPhoneNumbers == null ? ImmutableList.of(): ImmutableList.copyOf(targetPhoneNumbers);
        this.messageContent = messageContent;
    }

    @Nullable
    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public List<String> getTargetPhoneNumbers() {
        return targetPhoneNumbers;
    }

    @Nullable
    public String getMessageContent() {
        return messageContent;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GammuSendTextMessageRequest that = (GammuSendTextMessageRequest) o;
        return Objects.equals(authenticationToken, that.authenticationToken) &&
                Objects.equals(targetPhoneNumbers, that.targetPhoneNumbers) &&
                Objects.equals(messageContent, that.messageContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authenticationToken, targetPhoneNumbers, messageContent);
    }

    @Override
    public String toString() {
        return "GammuSendTextMessageRequest{" +
                "authenticationToken='" + authenticationToken + '\'' +
                ", targetPhoneNumbers=" + targetPhoneNumbers +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }
}
