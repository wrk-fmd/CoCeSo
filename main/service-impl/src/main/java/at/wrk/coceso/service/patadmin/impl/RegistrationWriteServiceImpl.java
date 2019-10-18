package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.RegistrationForm;
import at.wrk.coceso.service.patadmin.RegistrationWriteService;
import at.wrk.coceso.service.patadmin.internal.RegistrationServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RegistrationWriteServiceImpl implements RegistrationWriteService {

    @Autowired
    private RegistrationServiceInternal registrationService;

    @Autowired
    private EntityEventFactory entityEventFactory;

    @Override
    public Patient takeover(final int incidentId) {
        return NotifyList.execute(n -> registrationService.takeover(incidentId, n), entityEventFactory);
    }

    @Override
    public Patient update(final RegistrationForm form, final Concern concern) {
        return NotifyList.execute(n -> registrationService.update(form, concern, n), entityEventFactory);
    }
}
