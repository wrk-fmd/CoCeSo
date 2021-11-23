package at.wrk.coceso.service;

import at.wrk.coceso.dto.container.ContainerCreateDto;
import at.wrk.coceso.dto.container.ContainerDto;
import at.wrk.coceso.dto.container.ContainerUnitDto;
import at.wrk.coceso.dto.container.ContainerUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.Unit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface ContainerService {

    List<ContainerDto> getAll(Concern concern);

    ContainerDto getRoot(Concern concern);

    Container create(Concern concern, ContainerCreateDto data);

    void update(Concern concern, Container container, ContainerUpdateDto data);

    void remove(Concern concern, Container container);

    void updateUnit(Concern concern, Container container, Unit unit, ContainerUnitDto data);

    void removeUnit(Concern concern, Container container, Unit unit);
}
