package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entityevent.impl.NotifyListExecutor;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.TransportForm;
import at.wrk.coceso.service.patadmin.PostprocessingWriteService;
import at.wrk.coceso.service.patadmin.internal.PostprocessingServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PostprocessingWriteServiceImpl implements PostprocessingWriteService {

    private final PostprocessingServiceInternal postprocessingService;
    private final NotifyListExecutor notifyListExecutor;

    @Autowired
    public PostprocessingWriteServiceImpl(final PostprocessingServiceInternal postprocessingService, final NotifyListExecutor notifyListExecutor) {
        this.postprocessingService = postprocessingService;
        this.notifyListExecutor = notifyListExecutor;
    }

    @Override
    public Patient update(final PostprocessingForm form) {
        return notifyListExecutor.execute(n -> postprocessingService.update(form, n));
    }

    @Override
    public Patient discharge(final PostprocessingForm form) {
        return notifyListExecutor.execute(n -> postprocessingService.discharge(form, n));
    }

    @Override
    public Patient transported(final int patientId) {
        return notifyListExecutor.execute(n -> postprocessingService.transported(patientId, n));
    }

    @Override
    public Patient transport(final TransportForm form) {
        return notifyListExecutor.execute(n -> postprocessingService.transport(form, n));
    }
}
