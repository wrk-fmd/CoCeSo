package at.wrk.coceso.utils;

import at.wrk.coceso.entity.Patient;
import java.util.Collection;

public class Initializer {

  public static <T extends Patient> T incidents(T patient) {
    if (patient.getIncidents() != null) {
      patient.getIncidents().size();
    }
    return patient;
  }

  public static <T extends Collection<Patient>> T incidents(T patients) {
    patients.forEach(p -> incidents(p));
    return patients;
  }

}
