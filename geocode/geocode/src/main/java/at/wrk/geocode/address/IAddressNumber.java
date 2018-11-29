package at.wrk.geocode.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
// TODO Check why there is an interface with code for a data object...
public interface IAddressNumber {

    Integer getFrom();

    Integer getTo();

    String getLetter();

    String getBlock();

    @JsonIgnore
    default String getText() {
        if (getFrom() == null) {
            return null;
        }

        String str = "" + getFrom();
        if (getTo() != null) {
            str += "-" + getTo();
        } else if (getLetter() != null) {
            str += getLetter();
        }

        if (getBlock() != null) {
            str += "/" + getBlock();
        }

        return str;
    }

}
