package at.wrk.coceso.service.patadmin.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.Group;
import at.wrk.coceso.service.patadmin.PatadminService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PatadminServiceInternal extends PatadminService {

  List<Unit> update(List<Group> groups, Concern concern, User user, NotifyList notify);

}
