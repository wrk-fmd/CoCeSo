package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PostprocessingService {

    Patient getActivePatient(int patientId);

    Patient getTransported(int patientId);
}
