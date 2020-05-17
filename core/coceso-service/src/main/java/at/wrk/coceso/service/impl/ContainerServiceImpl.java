package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.container.ContainerCreateDto;
import at.wrk.coceso.dto.container.ContainerDto;
import at.wrk.coceso.dto.container.ContainerUnitDto;
import at.wrk.coceso.dto.container.ContainerUpdateDto;
import at.wrk.coceso.endpoint.ParamValidator;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.event.events.ContainerDeletedEvent;
import at.wrk.coceso.event.events.ContainerEvent;
import at.wrk.coceso.exceptions.ContainerCycleException;
import at.wrk.coceso.mapper.ContainerMapper;
import at.wrk.coceso.repository.ContainerRepository;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.ContainerService;
import at.wrk.fmd.mls.event.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
class ContainerServiceImpl implements ContainerService {

    private final ContainerRepository containerRepository;
    private final UnitRepository unitRepository;
    private final ContainerMapper containerMapper;
    private final EventBus eventBus;

    @Autowired
    public ContainerServiceImpl(final ContainerRepository containerRepository, final UnitRepository unitRepository,
            final ContainerMapper containerMapper, final EventBus eventBus) {
        this.containerRepository = containerRepository;
        this.unitRepository = unitRepository;
        this.containerMapper = containerMapper;
        this.eventBus = eventBus;
    }

    @Override
    public List<ContainerDto> getAll(Concern concern) {
        List<ContainerDto> containers = containerRepository.findByConcern(concern).stream()
                .map(containerMapper::containerToDto)
                .collect(Collectors.toList());
        containers.add(0, getRoot(concern));
        return containers;
    }

    @Override
    public ContainerDto getRoot(Concern concern) {
        List<Long> rootChildren = containerRepository.findRootChildren(concern).stream()
                .sorted(Comparator.comparing(Container::getOrdering))
                .map(Container::getId)
                .collect(Collectors.toList());

        List<Long> unassigned = unitRepository.findWithoutContainer(concern).stream()
                .sorted()
                .map(Unit::getId)
                .collect(Collectors.toList());

        ContainerDto root = new ContainerDto();
        root.setConcern(concern.getId());
        root.setChildren(rootChildren);
        root.setUnits(unassigned);
        return root;
    }

    @Override
    public Container create(Concern concern, ContainerCreateDto data) {
        Container parent = getInConcern(concern, data.getParent());

        Container container = new Container();
        container.setConcern(concern);
        container.setParent(parent);
        container.setName(data.getName());
        updateOrdering(concern, parent, container, data.getIndex());

        container = containerRepository.save(container);

        // Notify about the container and its new parent
        notify(concern, container);
        notify(concern, parent);

        return container;
    }

    @Override
    public void update(Concern concern, Container container, ContainerUpdateDto data) {
        Container previousParent = container.getParent();
        Container parent = data.getParent() != null ? getInConcern(concern, data.getParent()) : previousParent;

        if (!Objects.equals(previousParent, parent)) {
            // Make sure no cycles are introduced
            Set<Long> visited = new HashSet<>();
            for (Container cur = parent; cur != null; cur = cur.getParent()) {
                if (container.getId().equals(cur.getId()) || visited.contains(cur.getId())) {
                    // Container is ancestor of it's new parent or cycle already present
                    throw new ContainerCycleException();
                }
                visited.add(cur.getId());
            }

            container.setParent(parent);
        }

        if (data.getName() != null) {
            container.setName(data.getName());
        }

        if (data.getIndex() != null) {
            updateOrdering(concern, parent, container, data.getIndex());
        }

        // Notify about the container, its new parent, and its old parent
        notify(concern, container);
        notify(concern, parent);
        if (!Objects.equals(previousParent, parent)) {
            notify(concern, previousParent);
        }
    }

    @Override
    public void remove(Concern concern, Container container) {
        removeRecursive(container)
                .map(c -> new ContainerDeletedEvent(concern.getId(), c.getId()))
                .forEach(eventBus::publish);

        notify(concern, null);
        if (container.getParent() != null) {
            notify(concern, container.getParent());
        }
    }

    private Stream<Container> removeRecursive(Container container) {
        Stream<Container> removed = container.getChildren().stream().flatMap(this::removeRecursive);
        container.clearUnits();
        containerRepository.delete(container);
        return Stream.concat(Stream.of(container), removed);
    }

    @Override
    public void updateUnit(Concern concern, Container container, Unit unit, ContainerUnitDto data) {
        Container previous = unit.getContainer();
        unit.setContainer(container);

        // Get all units in the container except the updated one
        List<Unit> units = container.getUnits().stream()
                .filter(u -> !u.equals(unit))
                .sorted(Comparator.comparing(Unit::getOrdering))
                .collect(Collectors.toList());

        // Index must not exceed the list size
        int index = checkIndex(data.getIndex(), units.size());

        // Set ordering for the updated unit
        unit.setOrdering(index);

        // Set ordering for all other units
        for (int i = 0; i < units.size(); i++) {
            units.get(i).setOrdering(i < index ? i : i + 1);
        }

        // Notify about the new and old container
        notify(concern, container);
        if (!Objects.equals(previous, container)) {
            notify(concern, previous);
        }
    }

    @Override
    public void removeUnit(Concern concern, Container container, Unit unit) {
        if (!container.equals(unit.getContainer())) {
            // Not assigned to the given container
            return;
        }

        unit.setContainer(null);
        unit.setOrdering(null);

        // Notify the old container and the root
        notify(concern, container);
        notify(concern, null);
    }

    private Container getInConcern(final Concern concern, final Long id) {
        if (id == null || id == 0) {
            // No container set
            return null;
        }

        Container container = containerRepository.findById(id).orElse(null);
        ParamValidator.open(concern, container);
        return container;
    }

    private void updateOrdering(final Concern concern, final Container parent, final Container container, final Integer index) {
        // Load children of the parent
        Collection<Container> children = parent == null
                ? containerRepository.findRootChildren(concern)
                : parent.getChildren();

        // Get sorted list of neighbors of the updated container
        List<Container> neighbors = children.stream()
                .filter(c -> !c.equals(container))
                .sorted(Comparator.comparing(Container::getOrdering))
                .collect(Collectors.toList());

        // Index must not exceed the list size
        int checkedIndex = checkIndex(index, neighbors.size());

        // Set ordering for the updated container
        container.setOrdering(checkedIndex);

        // Set ordering for all other containers
        for (int i = 0; i < neighbors.size(); i++) {
            neighbors.get(i).setOrdering(i < checkedIndex ? i : i + 1);
        }
    }

    private int checkIndex(Integer index, int max) {
        if (index == null || index > max) {
            return max;
        }
        if (index < 0) {
            return 0;
        }
        return index;
    }

    private void notify(Concern concern, Container container) {
        ContainerDto dto = container != null ? containerMapper.containerToDto(container) : getRoot(concern);
        eventBus.publish(new ContainerEvent(dto));
    }
}
