package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
@Transactional
public interface PatadminService {

  boolean[] getAccessLevels(Concern concern);

  void addAccessLevels(ModelMap map, Concern concern);

  List<Patient> getAllInTreatment(Concern concern, User user);

  List<Patient> getPatientsByQuery(Concern concern, String query, boolean showDone, User user);

  List<Unit> getGroups(Concern concern);

  Unit getGroup(int id);

}
