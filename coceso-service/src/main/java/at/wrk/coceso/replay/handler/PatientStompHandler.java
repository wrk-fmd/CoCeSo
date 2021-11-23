package at.wrk.coceso.replay.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.patient.PatientDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.PatientService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class PatientStompHandler extends ConcernBoundAmqpHandler<PatientDto> {

    private final PatientService patientService;

    @Autowired
    public PatientStompHandler(final PatientService patientService, final ConcernService concernService, final AmqpTemplate amqpTemplate) {
        super(concernService, amqpTemplate, CocesoExchangeNames.STOMP_PATIENTS);
        this.patientService = patientService;
    }

    @Override
    protected Collection<PatientDto> getData(Concern concern) {
        return patientService.getAll(concern);
    }
}
