package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.SocketMessagingTemplate;
import at.wrk.coceso.entityevent.WebSocketWriter;
import at.wrk.coceso.entityevent.NotifyList;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.PatientSocketService;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PatientSocketServiceImpl implements PatientSocketService {

  @Autowired
  private PatientService patientService;

  private final EntityEventHandler<Patient> patientEventHandler;
  private final EntityEventListener<Patient> entityEventListener;

  @Autowired
  public PatientSocketServiceImpl(SocketMessagingTemplate template) {
    patientEventHandler = EntityEventHandler.getInstance(Patient.class);
    entityEventListener = patientEventHandler.addListener(new WebSocketWriter<>(template, "/topic/patient/main/%d", JsonViews.Main.class, null));
  }

  @PreDestroy
  public void destroy() {
    patientEventHandler.removeListener(entityEventListener);
  }

  @Override
  public Patient update(Patient patient, Concern concern, User user) {
    return NotifyList.execute(n -> patientService.update(patient, concern, user, n));
  }

}
