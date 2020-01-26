package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.entityevent.impl.NotifyListExecutor;
import at.wrk.coceso.form.Group;
import at.wrk.coceso.form.GroupsForm;
import at.wrk.coceso.service.patadmin.PatadminWriteService;
import at.wrk.coceso.service.patadmin.internal.PatadminServiceInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.List;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PatadminWriteServiceImpl implements PatadminWriteService {
    private static final Logger LOG = LoggerFactory.getLogger(PatadminWriteServiceImpl.class);

    @Autowired
    private PatadminServiceInternal patadminService;

    private final EntityEventHandler<Unit> unitEventHandler;
    private final EntityEventListener<Unit> entityEventListener;
    private final NotifyListExecutor notifyListExecutor;

    @Autowired
    public PatadminWriteServiceImpl(final EntityEventFactory entityEventFactory, final NotifyListExecutor notifyListExecutor) {
        unitEventHandler = entityEventFactory.getEntityEventHandler(Unit.class);
        entityEventListener = entityEventFactory.getWebSocketWriter(
                "/topic/patadmin/groups/%d",
                JsonViews.Patadmin.class,
                u -> (u.getType() != null && u.getType().isTreatment() ? null : u.getId()));
        this.notifyListExecutor = notifyListExecutor;
        unitEventHandler.addListener(entityEventListener);
    }

    @PreDestroy
    public void destroy() {
        unitEventHandler.removeListener(entityEventListener);
    }

    @Override
    public void update(GroupsForm form, Concern concern) {
        List<Group> groups = form.getGroups();

        if (groups != null) {
            notifyListExecutor.executeVoid(n -> patadminService.update(groups, concern, n));
        } else {
            LOG.warn("Tried to update empty group settings for patadmin. Update is ignored.");
        }
    }
}
