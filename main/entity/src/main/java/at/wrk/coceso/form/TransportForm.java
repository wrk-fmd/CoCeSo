package at.wrk.coceso.form;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.Ambulance;
import at.wrk.coceso.entity.enums.Naca;
import java.time.LocalDate;
import org.hibernate.validator.constraints.NotBlank;

public class TransportForm extends PostprocessingForm {

  @NotBlank
  private String ertype;

  private boolean priority;
  private Ambulance ambulance;

  public TransportForm() {
  }

  public TransportForm(Patient p) {
    super(p);
    ertype = p.getErtype();
    priority = false;
    ambulance = null;
  }

  @Override
  public String getInfo() {
    return super.getInfo();
  }

  @Override
  public String getDiagnosis() {
    return super.getDiagnosis();
  }

  @Override
  public Naca getNaca() {
    return super.getNaca();
  }

  @Override
  public LocalDate getBirthday() {
    return super.getBirthday();
  }

  @Override
  public String getExternalId() {
    return super.getExternalId();
  }

  @Override
  public String getFirstname() {
    return super.getFirstname();
  }

  @Override
  public String getLastname() {
    return super.getLastname();
  }

  @Override
  public int getPatient() {
    return super.getPatient();
  }

  public String getErtype() {
    return ertype;
  }

  public void setErtype(String ertype) {
    this.ertype = ertype;
  }

  public boolean isPriority() {
    return priority;
  }

  public void setPriority(boolean priority) {
    this.priority = priority;
  }

  public Ambulance getAmbulance() {
    return ambulance;
  }

  public void setAmbulance(Ambulance ambulance) {
    this.ambulance = ambulance;
  }

}
