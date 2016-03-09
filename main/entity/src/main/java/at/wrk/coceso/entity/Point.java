package at.wrk.coceso.entity;

import at.wrk.coceso.entity.helper.JsonViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Point implements Serializable {

  @JsonView(JsonViews.Always.class)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonView(JsonViews.Always.class)
  @Column(nullable = false, unique = true, updatable = false)
  private String info;

  @JsonView(JsonViews.Always.class)
  @Column
  private Double longitude;

  @JsonView(JsonViews.Always.class)
  @Column
  private Double latitude;

  public Point() {
  }

  public Point(String info) {
    this.info = info;
  }

  @PrePersist
  public void prePersist() {
    if (info == null) {
      info = "";
    }
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public void setLatLong(Point p) {
    this.latitude = p.latitude;
    this.longitude = p.longitude;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return (id == null && StringUtils.isEmpty(info) && (latitude == null || longitude == null));
  }

  public static boolean isEmpty(Point point) {
    return point == null || point.isEmpty();
  }

  public static String toStringOrNull(Point point) {
    return isEmpty(point) ? null : point.toString();
  }

  @Override
  public String toString() {
    if (!StringUtils.isEmpty(info)) {
      return info;
    }
    if (latitude != null && longitude != null) {
      return latitude + " " + longitude;
    }
    return "";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    Point p = (Point) obj;
    if (this.id != null && p.id != null && this.id.equals(p.id)) {
      return false;
    }

    return (Objects.equals(this.info, p.info)
            && Objects.equals(this.latitude, p.latitude) && Objects.equals(this.longitude, p.longitude));
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + java.util.Objects.hashCode(this.info);
    if (this.longitude != null) {
      hash = 11 * hash + (int) (Double.doubleToLongBits(this.longitude) ^ (Double.doubleToLongBits(this.longitude) >>> 32));
    }
    if (this.latitude != null) {
      hash = 11 * hash + (int) (Double.doubleToLongBits(this.latitude) ^ (Double.doubleToLongBits(this.latitude) >>> 32));
    }
    return hash;
  }

}
