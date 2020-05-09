package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.repository.ContainerRepository;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.ContainerService;
import at.wrk.coceso.utils.Initializer;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
class ContainerServiceImpl implements ContainerService {
  private static final Logger LOG = LoggerFactory.getLogger(ContainerServiceImpl.class);

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private UnitRepository unitRepository;

  @Autowired
  private ConcernService concernService;

  @Override
  public List<Container> getAll(final int concernId) {
    Concern concern = concernService.getById(concernId);

    List<Container> containers;
    if (concern != null) {
      containers = getAll(concern);
    } else {
      LOG.info("Failed to read concern for concernId '{}'.", concernId);
      containers =  ImmutableList.of();
    }

    return containers;
  }

  @Override
  public List<Container> getAll(Concern concern) {
    List<Container> container = containerRepository.findByConcern(concern);
    Container root = container.stream().filter(c -> c.getParent() == null).findFirst().orElseGet(Container::new);
    root.setSpare(getSpare(concern));
    if (root.getId() == null) {
      container.add(root);
    }
    return container;
  }

  @Override
  public Container getRoot(Concern concern) {
    Container container = containerRepository.findRootByConcern(concern);
    if (container == null) {
      container = new Container();
    }
    container.setSpare(getSpare(concern));
    return container;
  }

  @Override
  public Set<Integer> getSpare(Concern concern) {
    return unitRepository.findSpare(concern)
            .stream()
            .map(Unit::getId)
            .collect(Collectors.toSet());
  }

  @Override
  public synchronized Container doUpdate(Container container, Concern concern) {
    return Initializer.init(containerRepository.saveAndFlush(container.getId() == null ? prepareCreate(container, concern) : prepareUpdate(container)),
        Container::getConcern);
  }

  @Override
  public synchronized Container doRemove(int containerId) {
    Container container = containerRepository.findOne(containerId);
    if (container == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (container.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }

    containerRepository.delete(container);
    return Initializer.init(container, Container::getConcern);
  }

  @Override
  public synchronized ContainerPair doUpdateUnit(int containerId, int unitId, double ordering) {
    Unit unit = unitRepository.findOne(unitId);
    Container container = containerRepository.findOne(containerId);

    if (unit == null || container == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (unit.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }
    if (!unit.getConcern().equals(container.getConcern())) {
      throw new ErrorsException(Errors.ConcernMismatch);
    }

    ContainerPair ret = new ContainerPair();

    Container previous = unit.getContainer();
    if (previous == null) {
      ret.notifyRoot = true;
    } else if (!previous.getId().equals(container.getId())) {
      unit.setContainer(null);
      previous.removeUnit(unit);
      ret.previous = containerRepository.saveAndFlush(previous);
    }

    container.addUnit(unit, ordering);
    ret.newcont = Initializer.init(containerRepository.saveAndFlush(container), Container::getConcern);

    return ret;
  }

  @Override
  public synchronized Container doRemoveUnit(int unitId) {
    Unit unit = unitRepository.findOne(unitId);
    if (unit == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (unit.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }

    if (unit.getContainer() == null) {
      return null;
    }

    unit.getContainer().removeUnit(unit);
    containerRepository.saveAndFlush(unit.getContainer());
    return Initializer.init(unit.getContainer(), Container::getConcern);
  }

  private Container prepareCreate(Container container, Concern concern) {
    if (concernService.isClosed(concern)) {
      throw new ErrorsException(Errors.ConcernClosed);
    }

    container.setConcern(concern);

    if (container.getParentSlim() == null) {
      // Only allow one root container
      if (containerRepository.findRootByConcern(container.getConcern()) != null) {
        throw new ErrorsException(Errors.ContainerMultipleRoots);
      }
    } else {
      Container parent = containerRepository.findOne(container.getParentSlim());
      if (parent == null || !container.getConcern().equals(parent.getConcern())) {
        throw new ErrorsException(Errors.ConcernMismatch);
      }
    }

    container.emptyUnits();

    return container;
  }

  private Container prepareUpdate(Container container) {
    Container old = containerRepository.findOne(container.getId());
    if (old.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }

    if (container.getParentSlim() != null && !container.getParentSlim().equals(old.getParentSlim())) {
      Container parent = containerRepository.findOne(container.getParentSlim());
      if (parent == null || !old.getConcern().equals(parent.getConcern())) {
        // New parent does not exist or has wrong concern
        throw new ErrorsException(Errors.ConcernMismatch);
      }

      Set<Integer> visited = new HashSet<>();
      for (Container cur = parent; cur != null; cur = cur.getParent()) {
        if (container.getId().equals(cur.getId()) || visited.contains(cur.getId())) {
          // Container is ancestor of it's new parent or cycle already present
          throw new ErrorsException(Errors.ContainerCycle);
        }
        visited.add(cur.getId());
      }

      old.setParent(parent);
    }

    old.setName(container.getName());
    old.setOrdering(container.getOrdering());

    return old;
  }

}
