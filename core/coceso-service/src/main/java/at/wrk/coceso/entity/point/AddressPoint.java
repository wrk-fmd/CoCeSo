//package at.wrk.coceso.entity.point;
//
//import at.wrk.coceso.entity.helper.JsonViews;
//import at.wrk.geocode.Geocoder;
//import at.wrk.geocode.LatLng;
//import at.wrk.geocode.address.Address;
//import at.wrk.geocode.address.AddressNumber;
//import at.wrk.geocode.address.ImmutableAddress;
//import com.fasterxml.jackson.annotation.JsonView;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.beans.factory.annotation.Qualifier;
//
//import java.util.Objects;
//
///**
// * A Point representing an address
// */
//@Configurable
//class AddressPoint implements Point, Address {
//    private static final Logger LOG = LoggerFactory.getLogger(AddressPoint.class);
//
//    // TODO Using @Qualifier here feels kinda like hardcoding, maybe define that somewhere else
//    @Autowired
//    @Qualifier("ChainedGeocoder")
//    private Geocoder<ImmutableAddress> addressGeocoder;
//
//    private final String title;
//    private final String street;
//    private final String intersection;
//    private final String city;
//    private final String additional;
//    private final Integer postCode;
//
//    private boolean filled = false;
//    private LatLng coordinates;
//
//    private final AddressNumber number;
//
//    private AddressPoint() {
//        title = null;
//        street = null;
//        intersection = null;
//        number = null;
//        postCode = null;
//        city = null;
//        additional = null;
//    }
//
//    private AddressPoint(final AddressPoint other) {
//        // Create a "deep" copy of other - since all properties are effectively immutable they don't actually have to be copied
//        super();
//        filled = other.filled;
//        title = other.title;
//        street = other.street;
//        intersection = other.intersection;
//        number = other.number;
//        postCode = other.postCode;
//        city = other.city;
//        additional = other.additional;
//        coordinates = other.coordinates;
//        addressGeocoder = other.addressGeocoder;
//    }
//
//    public AddressPoint(
//            final String title,
//            final String street,
//            final String intersection,
//            final String city,
//            final String additional,
//            final Integer postCode,
//            final AddressNumber number) {
//        this.title = title;
//        this.street = street;
//        this.intersection = intersection;
//        this.city = city;
//        this.additional = additional;
//        this.postCode = postCode;
//        this.number = number;
//    }
//
//    @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
//    @Override
//    public String getStreet() {
//        return street;
//    }
//
//    @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
//    @Override
//    public String getIntersection() {
//        return intersection;
//    }
//
//    @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
//    @Override
//    public AddressNumber getNumber() {
//        return number;
//    }
//
//    @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
//    @Override
//    public Integer getPostCode() {
//        return postCode;
//    }
//
//    @JsonView({JsonViews.Database.class, JsonViews.PointFull.class})
//    @Override
//    public String getCity() {
//        return city;
//    }
//
//    @JsonView(JsonViews.PointMinimal.class)
//    @Override
//    public String getInfo() {
//        return Address.super.getInfo();
//    }
//
//    @Override
//    public String getInfo(String newline) {
//        String str = "";
//        if (title != null) {
//            str += title;
//        }
//
//        String address = Address.super.getInfo(newline);
//        if (!address.isEmpty() && !str.isEmpty()) {
//            str += newline;
//        }
//        str += address;
//
//        if (additional != null) {
//            if (!str.isEmpty()) {
//                str += newline;
//            }
//            str += additional;
//        }
//        return str;
//    }
//
//    @JsonView({JsonViews.Database.class, JsonViews.PointMinimal.class})
//    @Override
//    public LatLng getCoordinates() {
//        return coordinates;
//    }
//
//    @Override
//    public void tryToResolveExternalData() {
//        if (!filled && coordinates == null && !isEmpty()) {
//            filled = true;
//            LOG.debug("Address point was not resolved yet. Address geocoder is called for: {}", this);
//
//            ImmutableAddress addressToSearch = ImmutableAddress.createFromAddress(this);
//            coordinates = addressGeocoder.geocode(addressToSearch);
//        }
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return StringUtils.isEmpty(this.title) && StringUtils.isEmpty(this.street);
//    }
//
//    @Override
//    public AddressPoint deepCopy() {
//        return new AddressPoint(this);
//    }
//
//    @Override
//    public String toString() {
//        return getInfo(", ");
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 59 * hash + Objects.hashCode(this.title);
//        hash = 59 * hash + Objects.hashCode(this.street);
//        hash = 59 * hash + Objects.hashCode(this.intersection);
//        hash = 59 * hash + Objects.hashCode(this.number);
//        hash = 59 * hash + Objects.hashCode(this.postCode);
//        hash = 59 * hash + Objects.hashCode(this.city);
//        hash = 59 * hash + Objects.hashCode(this.additional);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null || getClass() != obj.getClass()) {
//            return false;
//        }
//        final AddressPoint other = (AddressPoint) obj;
//        return Objects.equals(this.title, other.title)
//                && Objects.equals(this.street, other.street)
//                && Objects.equals(this.intersection, other.intersection)
//                && Objects.equals(this.number, other.number)
//                && Objects.equals(this.postCode, other.postCode)
//                && Objects.equals(this.city, other.city)
//                && Objects.equals(this.additional, other.additional);
//    }
//}
