package at.wrk.coceso.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.*;

@Entity
@Table(name = "container")
public class Container implements Serializable, ConcernBoundEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "concern_fk", updatable = false, nullable = false)
  private Concern concern;

  private double ordering;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent")
  private Container parent;

  @Column(length = 60, nullable = false)
  private String name;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "unit_in_container", joinColumns = {
    @JoinColumn(name = "container_fk")})
  @MapKeyJoinColumn(name = "unit_fk")
  @Column(name = "ordering")
  private Map<Unit, Double> units;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Transient
  private Set<Integer> spare;

  public Container() {
  }

  public Container(int id) {
    this.id = id;
  }

  @PrePersist
  @PreUpdate
  public void prePersist() {
    if (name == null) {
      name = "";
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Container)) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    return (this.id != null && this.id.equals(((Container) obj).getId()));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return String.format("#%d (%s)", id, name);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public Concern getConcern() {
    return concern;
  }

  public void setConcern(Concern concern) {
    this.concern = concern;
  }

  public double getOrdering() {
    return ordering;
  }

  public void setOrdering(double ordering) {
    this.ordering = ordering;
  }

  public Container getParent() {
    return parent;
  }

  @JsonProperty("parent")
  public Integer getParentSlim() {
    return parent == null ? null : parent.getId();
  }

  public void setParent(Container parent) {
    this.parent = parent;
  }

  @JsonProperty("parent")
  public void setParentSlim(Integer parent) {
    this.parent = parent == null ? null : new Container(parent);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<Integer, Double> getUnits() {
    return units == null ? null : units.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
  }

  public void setUnits(Map<Integer, Double> units) {
  }

  public void removeUnit(Unit unit) {
    units.remove(unit);
  }

  public void addUnit(Unit unit, Double ordering) {
    if (units == null) {
      units = new HashMap<>();
    }
    units.put(unit, ordering);
  }

  public void emptyUnits() {
    if (units != null) {
      this.units.clear();
    }
  }

  public Set<Integer> getSpare() {
    return spare;
  }

  public void setSpare(Set<Integer> spare) {
    this.spare = spare;
  }

}
