package at.wrk.coceso.service.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.PatientService;

public interface PatientServiceInternal extends PatientService {

  Patient update(Patient patient, Concern concern, NotifyList notify);

  Patient updateAndDischarge(Patient patient, NotifyList notify);

  Patient discharge(Patient patient, NotifyList notify);

}
