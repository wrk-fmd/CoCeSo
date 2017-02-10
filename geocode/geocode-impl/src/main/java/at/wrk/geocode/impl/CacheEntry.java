package at.wrk.geocode.impl;

import at.wrk.geocode.address.Address;
import at.wrk.geocode.LatLng;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a database entry for caching a geocoding result (adress and corresponding coordinates)
 */
@Entity
@Table(name = "geocode")
class CacheEntry implements Serializable, Address, Address.Number {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String street;

  @Column
  private String intersection;

  @Column
  private Integer numberFrom;

  @Column
  private Integer numberTo;

  @Column
  private String numberLetter;

  @Column
  private String numberBlock;

  @Column
  private Integer postCode;

  @Column
  private String city;

  @Column
  private double lat;

  @Column
  private double lng;

  protected CacheEntry() {
  }

  public CacheEntry(Address address) {
    this(address, null);
  }

  public CacheEntry(Address address, LatLng coordinates) {
    this.street = address.getStreet();
    this.intersection = address.getIntersection();

    Address.Number number = address.getNumber();
    if (number != null) {
      this.numberFrom = number.getFrom();
      this.numberTo = number.getTo();
      this.numberLetter = number.getLetter();
      this.numberBlock = number.getBlock();
    }

    this.postCode = address.getPostCode();
    this.city = address.getCity();

    if (coordinates != null) {
      this.lat = coordinates.getLat();
      this.lng = coordinates.getLng();
    }
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
  public Address.Number getNumber() {
    return this;
  }

  @Override
  public Integer getFrom() {
    return numberFrom;
  }

  @Override
  public Integer getTo() {
    return numberTo;
  }

  @Override
  public String getLetter() {
    return numberLetter;
  }

  @Override
  public String getBlock() {
    return numberBlock;
  }

  @Override
  public Integer getPostCode() {
    return postCode;
  }

  @Override
  public String getCity() {
    return city;
  }

  public LatLng getCoordinates() {
    return new LatLng(lat, lng);
  }

}
