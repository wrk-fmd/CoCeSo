package at.wrk.coceso.service.patadmin.internal;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.form.Group;
import at.wrk.coceso.service.patadmin.PatadminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface PatadminServiceInternal extends PatadminService {

    List<Unit> update(List<Group> groups, Concern concern, NotifyList notify);
}
