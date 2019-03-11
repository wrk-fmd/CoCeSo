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

    public NiuSendTextMessageRequest(final List<String> numbers, final String message) {
        this.numbers = numbers == null ? ImmutableList.of(): ImmutableList.copyOf(numbers);
        this.message = message;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NiuSendTextMessageRequest that = (NiuSendTextMessageRequest) o;
        return Objects.equals(numbers, that.numbers) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers, message);
    }

    @Override
    public String toString() {
        return "NiuSendTextMessageRequest{" +
                "numbers=" + numbers +
                ", message='" + message + '\'' +
                '}';
    }
}
