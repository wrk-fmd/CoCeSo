package at.wrk.geocode.address;

import java.io.Serializable;

public class ImmutableAddress implements Address, Serializable {
    private static final long serialVersionUID = 1L;

    private final String street;
    private final String intersection;
    private final ImmutableAddressNumber number;
    private final Integer postCode;
    private final String city;

    public static ImmutableAddress createFromAddress(final Address address) {
        return new ImmutableAddress(
                address.getStreet(),
                address.getIntersection(),
                ImmutableAddressNumber.createFromAddressNumber(address.getNumber()),
                address.getPostCode(),
                address.getCity());
    }

    public ImmutableAddress(
            final String street,
            final String intersection,
            final ImmutableAddressNumber number,
            final Integer postCode,
            final String city) {
        this.street = street;
        this.intersection = intersection;
        this.number = number;
        this.postCode = postCode;
        this.city = city;
    }

    @Override
    public String getStreet() {
        return street;
    }

    @Override
    public String getIntersection() {
        return intersection;
    }

    @Override
    public ImmutableAddressNumber getNumber() {
        return number;
    }

    @Override
    public Integer getPostCode() {
        return postCode;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "ImmutableAddress{" +
                "street='" + street + '\'' +
                ", intersection='" + intersection + '\'' +
                ", number=" + number +
                ", postCode=" + postCode +
                ", city='" + city + '\'' +
                '}';
    }
}
