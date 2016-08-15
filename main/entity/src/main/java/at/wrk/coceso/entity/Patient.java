package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.Naca;
import at.wrk.coceso.entity.enums.Sex;
import at.wrk.coceso.entity.helper.JsonViews;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Patient implements Serializable, ConcernBoundEntity {

  @JsonView(JsonViews.Always.class)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "concern_fk", updatable = false, nullable = false)
  private Concern concern;

  @JsonView(JsonViews.Always.class)
  @Column(nullable = false, length = 64)
  private String lastname;

  @JsonView(JsonViews.Always.class)
  @Column(nullable = false, length = 64)
  private String firstname;

  @JsonView(JsonViews.Always.class)
  @Column(nullable = false, length = 40)
  private String externalId;

  @JsonView(JsonViews.Always.class)
  @Column
  private Sex sex;

  @JsonView(JsonViews.Always.class)
  @Column(nullable = false, length = 40)
  private String insurance;

  @JsonView(JsonViews.Always.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Column
  private LocalDate birthday;

  @JsonView(JsonViews.Patadmin.class)
  @Column
  private Naca naca;

  @JsonView(JsonViews.Always.class)
  @Column(nullable = false)
  private String diagnosis;

  @JsonView(JsonViews.Always.class)
  @Column(nullable = false, length = 40)
  private String ertype;

  @JsonView(JsonViews.Always.class)
  @Column(nullable = false)
  private String info;

  @JsonIgnore
  @Column
  private boolean done;

  @ManyToOne
  @JoinColumn(name = "medinfo_fk")
  private Medinfo medinfo;

  @JsonIgnore
  @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
  private Set<Incident> incidents;

  public Patient() {
  }

  public Patient(int id) {
    this.id = id;
  }

  @PrePersist
  @PreUpdate
  public void prePersist() {
    if (lastname == null) {
      lastname = "";
    }
    if (firstname == null) {
      firstname = "";
    }
    if (externalId == null) {
      externalId = "";
    }
    if (insurance == null) {
      insurance = "";
    }
    if (diagnosis == null) {
      diagnosis = "";
    }
    if (ertype == null) {
      ertype = "";
    }
    if (info == null) {
      info = "";
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
    return (this.id != null && this.id.equals(((Patient) obj).id));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
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

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }

  public String getInsurance() {
    return insurance;
  }

  public void setInsurance(String insurance) {
    this.insurance = insurance;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public Naca getNaca() {
    return naca;
  }

  public void setNaca(Naca naca) {
    this.naca = naca;
  }

  public String getDiagnosis() {
    return diagnosis;
  }

  public void setDiagnosis(String diagnosis) {
    this.diagnosis = diagnosis;
  }

  public String getErtype() {
    return ertype;
  }

  public void setErtype(String ertype) {
    this.ertype = ertype;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public Medinfo getMedinfo() {
    return medinfo;
  }

  public void setMedinfo(Medinfo medinfo) {
    this.medinfo = medinfo;
  }

  public Set<Incident> getIncidents() {
    return incidents;
  }

  @JsonIgnore
  public String getFullName() {
    return StringUtils.trimToEmpty(StringUtils.isNotBlank(externalId)
        ? String.format("%s %s (%s)", StringUtils.trimToEmpty(lastname), StringUtils.trimToEmpty(firstname), StringUtils.trimToEmpty(externalId))
        : String.format("%s %s", StringUtils.trimToEmpty(lastname), StringUtils.trimToEmpty(firstname)));
  }

  public Set<Unit> getGroup() {
    if (incidents == null) {
      return null;
    }

    return incidents.stream()
        .filter(i -> i.getType() == IncidentType.Treatment && !i.getState().isDone())
        .flatMap(i -> i.getUnits().keySet().stream())
        .collect(Collectors.toSet());
  }

  @JsonProperty("group")
  @JsonView(JsonViews.Patadmin.class)
  public Integer getGroupId() {
    if (incidents == null) {
      return null;
    }

    return incidents.stream()
        .filter(i -> i.getType() == IncidentType.Treatment && !i.getState().isDone())
        .flatMap(i -> i.getUnits().keySet().stream())
        .findFirst()
        .map(Unit::getId)
        .orElse(null);
  }

  @JsonIgnore
  public Set<String> getHospital() {
    if (incidents == null) {
      return null;
    }

    return incidents.stream()
        .filter(i -> i.getType() == IncidentType.Transport && !Point.isEmpty(i.getAo()))
        .map(i -> i.getAo().toString())
        .collect(Collectors.toSet());
  }

  @JsonIgnore
  public boolean isTransport() {
    if (incidents == null) {
      return false;
    }

    return incidents.stream().anyMatch(i -> i.getType() == IncidentType.Transport);
  }

  @Override
  public String toString() {
    return String.format("#%d: %s", id, getFullName());
  }

}
