package at.wrk.coceso.alarm.text.sender.sms.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Objects;

/**
 * Uses internal API.
 */
public class NiuSendTextMessageRequest implements SendTextMessageRequest {
    private static final long serialVersionUID = 1L;

    private final List<String> numbers;
    private final String message;
    private final boolean waitForDelivery;

    public NiuSendTextMessageRequest(final List<String> numbers, final String message, final boolean waitForDelivery) {
        this.numbers = numbers == null ? List.of(): List.copyOf(numbers);
        this.message = message;
        this.waitForDelivery = waitForDelivery;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public String getMessage() {
        return message;
    }

    public boolean isWaitForDelivery() {
        return waitForDelivery;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NiuSendTextMessageRequest that = (NiuSendTextMessageRequest) o;
        return Objects.equals(numbers, that.numbers) &&
                Objects.equals(message, that.message) &&
                Objects.equals(waitForDelivery, that.waitForDelivery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers, message, waitForDelivery);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("numbers", numbers)
                .append("message", message)
                .append("waitForDelivery", waitForDelivery)
                .toString();
    }
}
