package at.wrk.coceso.entity;

import at.wrk.coceso.entity.helper.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Concern implements Serializable {

  @JsonView({JsonViews.Edit.class, JsonViews.Home.class})
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonView(JsonViews.Always.class)
  @Length(max = 100, message = "concern.name.length")
  @NotBlank(message = "concern.name.notempty")
  @Column(nullable = false, length = 100, unique = true)
  private String name;

  @JsonView(JsonViews.Edit.class)
  @Column(nullable = false)
  private String info;

  @JsonView(JsonViews.Home.class)
  @Column
  private boolean closed;

  @JsonView({JsonViews.Main.class, JsonViews.Edit.class})
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "sections", joinColumns = {
    @JoinColumn(name = "concern_fk")})
  @Column(name = "name", length = 30)
  private Set<String> sections;

  public Concern() {
    closed = false;
  }

  public Concern(int id) {
    this.id = id;
  }

  @PrePersist
  @PreUpdate
  public void prePersist() {
    if (info == null) {
      info = "";
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != Hibernate.getClass(obj)) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    return (this.id != null && this.id.equals(((Concern) obj).getId()));
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  public static boolean isClosed(Concern concern) {
    return concern == null || concern.getId() == null || concern.isClosed();
  }

  public Set<String> getSections() {
    return sections;
  }

  public boolean containsSection(final String section) {
    return sections != null && sections.contains(section);
  }

  public void addSection(final String section) {
    if (sections == null) {
      sections = new HashSet<>();
    }
    sections.add(section);
  }

  public void removeSection(final String section) {
    if (sections != null) {
      sections.remove(section);
    }
  }

}
