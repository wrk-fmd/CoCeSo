package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface InfoService {

  Page<Patient> getAll(Concern concern, Pageable pageable, User user);

  Page<Patient> getByQuery(Concern concern, String query, Pageable pageable, User user);

}
