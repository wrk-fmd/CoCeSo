package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface ContainerWriteService {

  public Container update(Container container, Concern concern);

  public void remove(int containerId);

  public void updateUnit(int containerId, int unitId, double ordering);

  public void removeUnit(int unitId);

  public void notifyRoot(Concern concern);

}
