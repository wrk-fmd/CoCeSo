package at.wrk.coceso.form;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.Naca;
import at.wrk.coceso.entity.enums.Sex;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class RegistrationForm {

  private Integer patient;
  private String lastname;
  private String firstname;
  private String externalId;
  private Integer group;
  private Naca naca;
  private String diagnosis;
  private String info;
  private String insurance;
  private Sex sex;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate birthday;

  public RegistrationForm() {
  }

  public RegistrationForm(Patient p) {
    patient = p.getId();
    lastname = p.getLastname();
    firstname = p.getFirstname();
    externalId = p.getExternalId();
    birthday = p.getBirthday();
    group = p.getGroupId();
    naca = p.getNaca();
    info = p.getInfo();
    diagnosis = p.getDiagnosis();
    insurance = p.getInsurance();
    sex = p.getSex();
  }

  public Integer getPatient() {
    return patient;
  }

  public void setPatient(Integer patient) {
    this.patient = patient;
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

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public Integer getGroup() {
    return group;
  }

  public void setGroup(Integer group) {
    this.group = group;
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

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getInsurance() {
    return insurance;
  }

  public void setInsurance(String insurance) {
    this.insurance = insurance;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }
}
