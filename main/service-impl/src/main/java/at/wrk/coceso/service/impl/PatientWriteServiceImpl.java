package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.service.internal.PatientServiceInternal;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import at.wrk.coceso.service.PatientWriteService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PatientWriteServiceImpl implements PatientWriteService {

  @Autowired
  private PatientServiceInternal patientService;

  private final EntityEventFactory eef;
  private final EntityEventHandler<Patient> patientEventHandler;
  private final EntityEventListener<Patient> entityEventListener;

  @Autowired
  public PatientWriteServiceImpl(EntityEventFactory eef) {
    this.eef = eef;
    patientEventHandler = eef.getEntityEventHandler(Patient.class);
    entityEventListener = eef.getWebSocketWriter("/topic/patient/main/%d", JsonViews.Main.class, null);
    patientEventHandler.addListener(entityEventListener);
  }

  @PreDestroy
  public void destroy() {
    patientEventHandler.removeListener(entityEventListener);
  }

  @Override
  public Patient update(Patient patient, Concern concern, User user) {
    return NotifyList.execute(n -> patientService.update(patient, concern, user, n), eef);
  }

}
