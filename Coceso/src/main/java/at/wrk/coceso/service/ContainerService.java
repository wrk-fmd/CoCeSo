package at.wrk.coceso.service;

import at.wrk.coceso.dao.UnitContainerDao;
import at.wrk.coceso.entity.helper.UnitContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContainerService {
    @Autowired
    UnitContainerDao containerDao;

    /**
     * Id <= 0 for New, Id > 0 for Update: ordering == -2 for Delete
     * @param container Container to update
     * @return Id of container, -1 for Error
     */
    public synchronized int update(UnitContainer container) {
        if(container.getId() <= 0) {
            return containerDao.addContainer(container.getHead(), container.getName(), container.getOrdering());
        }
        if(container.getOrdering() == -2) {
            return containerDao.removeContainer(container.getId()) ? 0 : -1;
        }
        return containerDao.updateContainer(container) ? 0 : -1;
    }

    /**
     *
     * @param containerId Id
     * @param unitId Id
     * @param ordering == -2 for Deletion
     * @return Success
     */
    public synchronized boolean updateUnit(int containerId, int unitId, double ordering) {
        containerDao.resetUnit(unitId);
        if(ordering == -2) {
            return true;
        }
        return containerDao.addUnit(containerId, unitId, ordering);
    }
}
