package at.wrk.coceso.service.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface MedinfoService {

  Medinfo getById(int id, User user);

  List<Medinfo> getAllByQuery(Concern concern, String query, User user);

  List<Medinfo> getForAutocomplete(Concern concern, String query, String field, User user);

  int deleteAll(Concern concern);

  List<Medinfo> save(Iterable<Medinfo> medinfos);

}
