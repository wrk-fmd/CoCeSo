package at.wrk.geocode.address;

import java.io.Serializable;

public class ImmutableAddressNumber implements IAddressNumber, Serializable {
    private static final long serialVersionUID = 1L;

    private final Integer from;
    private final Integer to;
    private final String letter;
    private final String block;

    public static ImmutableAddressNumber createFromAddressNumber(final IAddressNumber number) {
        return number != null
                ? new ImmutableAddressNumber(number.getFrom(), number.getTo(), number.getLetter(), number.getBlock())
                : null;
    }

    public ImmutableAddressNumber(final Integer from, final Integer to, final String letter, final String block) {
        this.from = from;
        this.to = to;
        this.letter = letter;
        this.block = block;
    }

    @Override
    public Integer getFrom() {
        return from;
    }

    @Override
    public Integer getTo() {
        return to;
    }

    @Override
    public String getLetter() {
        return letter;
    }

    @Override
    public String getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "ImmutableAddressNumber{" +
                "from=" + from +
                ", to=" + to +
                ", letter='" + letter + '\'' +
                ", block='" + block + '\'' +
                '}';
    }
}
