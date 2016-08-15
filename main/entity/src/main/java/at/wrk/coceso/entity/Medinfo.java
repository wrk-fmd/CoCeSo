package at.wrk.coceso.entity;

import at.wrk.coceso.entity.types.MapUserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.Length;

@Entity
@TypeDef(name = "json", typeClass = MapUserType.class)
public class Medinfo implements Serializable, ConcernBoundEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "concern_fk", updatable = false)
  private Concern concern;

  @NotNull
  @Length(max = 64)
  @Column(nullable = false, length = 64)
  private String firstname;

  @NotNull
  @Length(max = 64)
  @Column(nullable = false, length = 64)
  private String lastname;

  @NotNull
  @Length(max = 64)
  @Column(nullable = false, length = 40)
  private String externalId;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Column
  private LocalDate birthday;

  @JsonIgnore
  @Column
  @Type(type = "json")
  private Map<String, Object> data;

  @JsonIgnore
  @OneToMany(mappedBy = "medinfo")
  private List<Patient> patients;

  public Medinfo() {
  }

  public Medinfo(Integer id) {
    this.id = id;
  }

  @PrePersist
  @PreUpdate
  public void prepareNotNull() {
    if (firstname == null) {
      firstname = "";
    }
    if (lastname == null) {
      lastname = "";
    }
    if (externalId == null) {
      externalId = "";
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
    return (this.id != null && this.id.equals(((Medinfo) obj).id));
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

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  public List<Patient> getPatients() {
    return patients;
  }

  @JsonIgnore
  public String getFullName() {
    return StringUtils.trimToEmpty(StringUtils.isNotBlank(externalId)
        ? String.format("%s %s (%s)", StringUtils.trimToEmpty(lastname), StringUtils.trimToEmpty(firstname), StringUtils.trimToEmpty(externalId))
        : String.format("%s %s", StringUtils.trimToEmpty(lastname), StringUtils.trimToEmpty(firstname)));
  }

  @Override
  public String toString() {
    return String.format("#%d: %s", id, getFullName());
  }

}
