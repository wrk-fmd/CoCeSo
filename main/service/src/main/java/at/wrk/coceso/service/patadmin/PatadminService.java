package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.List;

@Service
@Transactional
public interface PatadminService {

    boolean[] getAccessLevels(Concern concern);

    void addAccessLevels(ModelMap map, Concern concern);

    List<Patient> getAllInTreatment(Concern concern);

    List<Patient> getPatientsByQuery(Concern concern, String query, boolean showDone);

    List<Unit> getGroups(Concern concern);

    Unit getGroup(int id);
}
