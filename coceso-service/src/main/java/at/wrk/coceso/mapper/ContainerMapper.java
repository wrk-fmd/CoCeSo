package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.container.ContainerDto;
import at.wrk.coceso.entity.Container;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    @Mapping(target = "concern", source = "concern.id")
    @Mapping(target = "parent", source = "parent.id")
    @Mapping(target = "children", source = "sortedChildren")
    @Mapping(target = "units", source = "sortedUnits")
    ContainerDto containerToDto(Container container);
}
