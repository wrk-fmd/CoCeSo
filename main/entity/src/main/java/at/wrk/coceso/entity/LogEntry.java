package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.Changes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name = "log")
public class LogEntry implements Serializable, Comparable<LogEntry>, ConcernBoundEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "concern_fk", updatable = false, nullable = false)
  private Concern concern;

  @Column(nullable = false, updatable = false)
  private Timestamp timestamp;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "unit_fk")
  private Unit unit;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "incident_fk", updatable = false)
  private Incident incident;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "patient_fk", updatable = false)
  private Patient patient;

  @Column(name = "taskstate", updatable = false)
  private TaskState state;

  @Column(nullable = false)
  private LogEntryType type;

  @ManyToOne
  @JoinColumn(name = "user_fk", nullable = false, updatable = false)
  private User user;

  @Column
  private String text;

  @Column(updatable = false)
  private Changes changes;

  public LogEntry() {
  }

  public LogEntry(User user, LogEntryType type, String text, Concern concern, Unit unit, Incident incident, Patient patient, TaskState state, Changes changes) {
    this.user = user;
    this.type = type;
    this.text = text;
    this.concern = concern;
    this.unit = unit;
    this.incident = incident;
    this.patient = patient;
    this.state = state;
    this.changes = changes;
  }

  @PrePersist
  public void prePersist() {
    timestamp = new Timestamp(new Date().getTime());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    return (this.id != null && this.id.equals(((LogEntry) obj).id));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public int compareTo(LogEntry t) {
    if (timestamp == null || t == null || t.timestamp == null) {
      return 0;
    }
    return timestamp.compareTo(t.timestamp);
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

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public Unit getUnit() {
    return unit;
  }

  @JsonProperty("unit")
  public Integer getUnitSlim() {
    return unit == null ? null : unit.getId();
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  @JsonProperty("unit")
  public void setUnitSlim(Integer unitId) {
    this.unit = unitId == null ? null : new Unit(unitId);
  }

  public Incident getIncident() {
    return incident;
  }

  @JsonProperty("incident")
  public Integer getIncidentSlim() {
    return incident == null ? null : incident.getId();
  }

  public void setIncident(Incident incident) {
    this.incident = incident;
  }

  @JsonProperty("incident")
  public void setIncidentSlim(Integer incidentId) {
    this.incident = incidentId == null ? null : new Incident(incidentId);
  }

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

  public TaskState getState() {
    return state;
  }

  public void setState(TaskState state) {
    this.state = state;
  }

  public LogEntryType getType() {
    return type;
  }

  public void setType(LogEntryType type) {
    this.type = type;
  }

  public boolean isAuto() {
    return this.type != LogEntryType.CUSTOM;
  }

  @JsonIgnore
  public User getUser() {
    return user;
  }

  @JsonProperty("user")
  public String getUsername() {
    return user == null ? null : user.getUsername();
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Changes getChanges() {
    return changes;
  }

  public void setChanges(Changes changes) {
    this.changes = changes;
  }

}
