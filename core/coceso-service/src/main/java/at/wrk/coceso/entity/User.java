package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.Authority;
import at.wrk.coceso.entity.helper.JsonViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Serializable {

  @JsonView(JsonViews.Always.class)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonView(JsonViews.Always.class)
  @NotNull
  @Length(max = 64)
  @Column(nullable = false, length = 64)
  private String firstname;

  @JsonView(JsonViews.Always.class)
  @NotNull
  @Length(max = 64)
  @Column(nullable = false, length = 64)
  private String lastname;

  @JsonView(JsonViews.Always.class)
  @Column
  private int personnelId;

  @JsonView(JsonViews.Always.class)
  @NotNull
  @Column(nullable = false)
  private String contact;

  @JsonView(JsonViews.Always.class)
  @NotNull
  @Column(nullable = false)
  private String info;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "concern_fk")
  private Concern activeConcern;

  @JsonView(JsonViews.UserFull.class)
  @Column
  private boolean allowLogin;

  @JsonView(JsonViews.Always.class)
  @Length(max = 32)
  @Column(length = 32)
  private String username;

  @JsonIgnore
  @Column
  private String hashedPW;

  @JsonView(JsonViews.UserFull.class)
  @ElementCollection
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_fk"))
  @Column(name = "urole", nullable = false)
  private Set<Authority> internalAuthorities;

  public User() {
  }

  public User(int personnelId, String lastname, String firstname) {
    this.personnelId = personnelId;
    this.lastname = lastname;
    this.firstname = firstname;
  }

  @PrePersist
  @PreUpdate
  public void prePersist() {
    if (firstname == null) {
      firstname = "";
    }
    if (lastname == null) {
      lastname = "";
    }
    if (contact == null) {
      contact = "";
    }
    if (info == null) {
      info = "";
    }
    if (username == null || username.isEmpty()) {
      username = null;
      hashedPW = null;
      allowLogin = false;
      if (internalAuthorities != null) {
        internalAuthorities.clear();
      }
    }
  }

  @Override
  public String toString() {
    return username;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname == null ? null : firstname.trim();
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname == null ? null : lastname.trim();
  }

  public int getPersonnelId() {
    return personnelId;
  }

  public void setPersonnelId(int personnelId) {
    this.personnelId = personnelId;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact == null ? null : contact.trim();
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info == null ? null : info.trim();
  }

  public Concern getActiveConcern() {
    return activeConcern;
  }

  public void setActiveConcern(Concern activeConcern) {
    this.activeConcern = activeConcern;
  }

  public boolean isAllowLogin() {
    return allowLogin;
  }

  public void setAllowLogin(boolean allowLogin) {
    this.allowLogin = allowLogin;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username == null ? null : username.trim();
  }

  @JsonIgnore
  public String getPassword() {
    return hashedPW;
  }

  public void setHashedPW(String hashedPW) {
    this.hashedPW = hashedPW;
  }

  public Set<Authority> getInternalAuthorities() {
    return internalAuthorities;
  }

  public void setInternalAuthorities(Set<Authority> internalAuthorities) {
    this.internalAuthorities = internalAuthorities;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj != null && getClass() == obj.getClass()
        && this.id != null && this.id.equals(((User) obj).id));
  }

  @Override
  public int hashCode() {
    return this.id == null ? 0 : this.id;
  }
}
