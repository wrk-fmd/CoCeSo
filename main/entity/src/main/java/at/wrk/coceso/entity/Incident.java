package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.JsonViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Incident implements Serializable, Comparable<Incident>, ConcernBoundEntity {

  @JsonView(JsonViews.Always.class)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "concern_fk", updatable = false, nullable = false)
  private Concern concern;

  @JsonView(JsonViews.Main.class)
  @Column(nullable = false)
  private IncidentState state;

  @JsonView(JsonViews.Main.class)
  @Column
  private boolean priority;

  @JsonView(JsonViews.Main.class)
  @Column
  private boolean blue;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "task", joinColumns = {
    @JoinColumn(name = "incident_fk")})
  @MapKeyJoinColumn(name = "unit_fk")
  @Column(name = "state")
  private Map<Unit, TaskState> units;

  @JsonView(JsonViews.Main.class)
  @ManyToOne
  @JoinColumn(name = "bo_point_fk")
  private Point bo;

  @JsonView(JsonViews.Main.class)
  @ManyToOne
  @JoinColumn(name = "ao_point_fk")
  private Point ao;

  @JsonView(JsonViews.Main.class)
  @Column(nullable = false, length = 100)
  private String casusNr;

  @JsonView(JsonViews.Main.class)
  @Column(nullable = false)
  private String info;

  @JsonView(JsonViews.Main.class)
  @Column(nullable = false, length = 100)
  private String caller;

  @JsonView(JsonViews.Main.class)
  @Column(nullable = false)
  private IncidentType type;

  @JsonView(JsonViews.Main.class)
  @ManyToOne
  @JoinColumn(name = "patient_fk")
  private Patient patient;

  // TODO: FK relation!
  @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
  @Column(name = "section_fk")
  private String section;

  @JsonView(JsonViews.Main.class)
  @NotNull
  @Column(nullable = false, updatable = false)
  private OffsetDateTime created;

  @JsonView(JsonViews.Main.class)
  @Column
  private OffsetDateTime arrival;

  @JsonView(JsonViews.Main.class)
  @Column
  private OffsetDateTime stateChange;

  @JsonView(JsonViews.Main.class)
  @Column
  private OffsetDateTime ended;

  @Transient
  private Map<Integer, TaskState> addedUnits;

  public Incident() {
  }

  public Incident(int id) {
    this.id = id;
  }

  @PrePersist
  @PreUpdate
  public void prePersist() {
    if (state == null) {
      state = IncidentState.Open;
    }
    if (casusNr == null) {
      casusNr = "";
    }
    if (caller == null) {
      caller = "";
    }
    if (info == null) {
      info = "";
    }
    if (created == null) {
      created = OffsetDateTime.now();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    return (this.id != null && this.id.equals(((Incident) obj).id));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public int compareTo(Incident t) {
    if (t == null || t.id == null) {
      return id == null ? 0 : -1;
    }
    return id == null ? 1 : id.compareTo(t.id);
  }

  @Override
  public String toString() {
    return String.format("#%d", id);
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

  public IncidentState getState() {
    return state;
  }

  public void setState(IncidentState state) {
    this.state = state;
  }

  public boolean isPriority() {
    return priority;
  }

  public void setPriority(boolean priority) {
    this.priority = priority;
  }

  public boolean isBlue() {
    return blue;
  }

  public void setBlue(boolean blue) {
    this.blue = blue;
  }

  @JsonIgnore
  public Map<Unit, TaskState> getUnits() {
    return units;
  }

  @JsonView(JsonViews.Main.class)
  @JsonProperty("units")
  public Map<Integer, TaskState> getUnitsSlim() {
    Map<Integer, TaskState> slim = null;
    if (units != null) {
      slim = units.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue));
    }
    if (addedUnits != null) {
      if (slim == null) {
        slim = new HashMap<>(addedUnits);
      } else {
        slim.putAll(addedUnits);
      }
    }

    return slim;
  }

  @JsonProperty("units")
  public void setUnitsSlim(Map<Integer, TaskState> units) {
    this.units = units.entrySet().stream().collect(Collectors.toMap(entry -> new Unit(entry.getKey()), Map.Entry::getValue));
  }

  void addUnit(Unit unit, TaskState state) {
    if (addedUnits == null) {
      addedUnits = new HashMap<>();
    }
    addedUnits.put(unit.getId(), state);
  }

  void removeUnit(Unit unit) {
    if (units != null) {
      units.remove(unit);
    }
    if (addedUnits != null) {
      addedUnits.remove(unit.getId());
    }
  }

  public Point getBo() {
    return bo;
  }

  public void setBo(Point bo) {
    this.bo = bo;
  }

  public Point getAo() {
    return ao;
  }

  public void setAo(Point ao) {
    this.ao = ao;
  }

  public String getCasusNr() {
    return casusNr;
  }

  public void setCasusNr(String casusNr) {
    this.casusNr = casusNr;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getCaller() {
    return caller;
  }

  public void setCaller(String caller) {
    this.caller = caller;
  }

  public IncidentType getType() {
    return type;
  }

  public void setType(IncidentType type) {
    this.type = type;
  }

  @JsonIgnore
  public Patient getPatient() {
    return patient;
  }

  @JsonProperty("patient")
  public Integer getPatientSlim() {
    return patient == null ? null : patient.getId();
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  @JsonProperty("patient")
  public void setPatientSlim(Integer patient) {
    this.patient = patient == null ? null : new Patient(patient);
  }

  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public OffsetDateTime getCreated() {
    return created;
  }

  public OffsetDateTime getArrival() {
    return arrival;
  }

  public void setArrival() {
    arrival = OffsetDateTime.now();
  }

  public OffsetDateTime getStateChange() {
    return stateChange;
  }

  public void setStateChange() {
    stateChange = OffsetDateTime.now();
  }

  public OffsetDateTime getEnded() {
    return ended;
  }

  public void setEnded() {
    ended = OffsetDateTime.now();
  }

  @JsonIgnore
  public boolean isRelevant() {
    return type != IncidentType.Treatment
        && (!state.isDone() || (type != IncidentType.HoldPosition
        && type != IncidentType.ToHome && type != IncidentType.Standby));
  }

}
