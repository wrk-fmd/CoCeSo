package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.impl.NotifyListExecutor;
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

    private final RegistrationServiceInternal registrationService;
    private final NotifyListExecutor notifyListExecutor;

    @Autowired
    public RegistrationWriteServiceImpl(final NotifyListExecutor notifyListExecutor, final RegistrationServiceInternal registrationService) {
        this.notifyListExecutor = notifyListExecutor;
        this.registrationService = registrationService;
    }

    @Override
    public Patient takeover(final int incidentId) {
        return notifyListExecutor.execute(n -> registrationService.takeover(incidentId, n));
    }

    @Override
    public Patient update(final RegistrationForm form, final Concern concern) {
        return notifyListExecutor.execute(n -> registrationService.update(form, concern, n));
    }
}
