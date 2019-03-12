package at.wrk.coceso.alarm.text.sender.sms.data;

import com.google.common.collect.ImmutableList;

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
        this.numbers = numbers == null ? ImmutableList.of(): ImmutableList.copyOf(numbers);
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
        return "NiuSendTextMessageRequest{" +
                "numbers=" + numbers +
                ", message='" + message + '\'' +
                ", waitForDelivery=" + waitForDelivery +
                '}';
    }
}
