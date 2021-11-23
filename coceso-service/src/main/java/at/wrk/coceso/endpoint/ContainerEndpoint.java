package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.container.ContainerCreateDto;
import at.wrk.coceso.dto.container.ContainerDto;
import at.wrk.coceso.dto.container.ContainerUnitDto;
import at.wrk.coceso.dto.container.ContainerUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/concerns/{concern}/container")
public class ContainerEndpoint {

    private final ContainerService containerService;

    @Autowired
    public ContainerEndpoint(final ContainerService containerService) {
        this.containerService = containerService;
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONTAINER_READ)")
    @GetMapping
    public List<ContainerDto> getAllContainers(@PathVariable final Concern concern) {
        ParamValidator.open(concern);
        return containerService.getAll(concern);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONTAINER_EDIT)")
    @PostMapping
    public void createContainer(@PathVariable final Concern concern, @RequestBody final ContainerCreateDto data) {
        ParamValidator.open(concern);
        containerService.create(concern, data);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONTAINER_EDIT)")
    @PutMapping("/{container}")
    public void updateContainer(@PathVariable final Concern concern, @PathVariable final Container container,
            @RequestBody final ContainerUpdateDto data) {
        ParamValidator.open(concern, container);
        containerService.update(concern, container, data);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONTAINER_EDIT)")
    @DeleteMapping("/{container}")
    public void deleteContainer(@PathVariable final Concern concern, @PathVariable final Container container) {
        ParamValidator.open(concern, container);
        containerService.remove(concern, container);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONTAINER_EDIT)")
    @PutMapping("/{container}/units/{unit}")
    public void updateContainerUnit(@PathVariable final Concern concern, @PathVariable final Container container,
            @PathVariable final Unit unit, @RequestBody final ContainerUnitDto data) {
        ParamValidator.open(concern, container, unit);
        containerService.updateUnit(concern, container, unit, data);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONTAINER_EDIT)")
    @DeleteMapping("/{container}/units/{unit}")
    public void removeContainerUnit(@PathVariable final Concern concern, @PathVariable final Container container,
            @PathVariable final Unit unit) {
        ParamValidator.open(concern, container, unit);
        containerService.removeUnit(concern, container, unit);
    }
}
