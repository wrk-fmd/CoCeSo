package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyListExecutor;
import at.wrk.coceso.service.PatientWriteService;
import at.wrk.coceso.service.internal.PatientServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PatientWriteServiceImpl implements PatientWriteService {

  @Autowired
  private PatientServiceInternal patientService;

  private final EntityEventHandler<Patient> patientEventHandler;
  private final EntityEventListener<Patient> entityEventListener;
  private final NotifyListExecutor notifyListExecutor;

  @Autowired
  public PatientWriteServiceImpl(final EntityEventFactory entityEventFactory, final NotifyListExecutor notifyListExecutor) {
    patientEventHandler = entityEventFactory.getEntityEventHandler(Patient.class);
    entityEventListener = entityEventFactory.getWebSocketWriter("/topic/patient/main/%d", JsonViews.Main.class, null);
    this.notifyListExecutor = notifyListExecutor;
    patientEventHandler.addListener(entityEventListener);
  }

  @PreDestroy
  public void destroy() {
    patientEventHandler.removeListener(entityEventListener);
  }

  @Override
  public Patient update(final Patient patient, final Concern concern) {
    return notifyListExecutor.execute(n -> patientService.update(patient, concern, n));
  }
}
