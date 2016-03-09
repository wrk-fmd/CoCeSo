package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.Authority;
import at.wrk.coceso.entity.helper.JsonViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable {

  private final static PasswordEncoder encoder = new BCryptPasswordEncoder();

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
  @ManyToOne
  @JoinColumn(name = "concern_fk")
  private Concern activeConcern;

  @JsonView(JsonViews.Always.class)
  @Column
  private boolean allowLogin;

  @JsonView(JsonViews.Always.class)
  @Length(max = 32)
  @Column(length = 32)
  private String username;

  @JsonIgnore
  @Column
  private String hashedPW;

  @JsonView(JsonViews.Always.class)
  @ElementCollection(fetch = FetchType.EAGER)
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_fk"))
  @Column(name = "urole", nullable = false)
  private Set<Authority> internalAuthorities;

  @JsonIgnore
  @Transient
  private Collection<Authority> authorities;

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
      authorities = null;
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

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return allowLogin;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return allowLogin;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return allowLogin;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return allowLogin;
  }

  public void setAllowLogin(boolean allowLogin) {
    this.allowLogin = allowLogin;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username == null ? null : username.trim();
  }

  @Override
  @JsonIgnore
  public String getPassword() {
    return hashedPW;
  }

  public boolean validatePassword(String password) {
    if (hashedPW == null || hashedPW.isEmpty()) {
      return false;
    }
    return encoder.matches(password, hashedPW);
  }

  public void setHashedPW(String hashedPW) {
    this.hashedPW = hashedPW;
  }

  public void setPassword(String password) {
    hashedPW = encoder.encode(password);
  }

  public Set<Authority> getInternalAuthorities() {
    return internalAuthorities;
  }

  public void setInternalAuthorities(Set<Authority> internalAuthorities) {
    this.internalAuthorities = internalAuthorities;
    this.authorities = null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (authorities == null) {
      authorities = internalAuthorities.stream()
              .flatMap(a -> a.getAuthorities().stream())
              .collect(Collectors.toSet());
    }
    return authorities;
  }

  public void setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
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
