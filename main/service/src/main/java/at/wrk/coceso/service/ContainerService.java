package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface ContainerService {

  List<Container> getAll(Concern concern);

  Container getRoot(Concern concern);

  Container doUpdate(Container container, Concern concern);

  Container doRemove(int containerId);

  ContainerPair doUpdateUnit(int containerId, int unitId, double ordering);

  Container doRemoveUnit(int unitId);

  static class ContainerPair {

    public boolean notifyRoot = false;
    public Container previous = null;
    public Container newcont = null;
  }

}
