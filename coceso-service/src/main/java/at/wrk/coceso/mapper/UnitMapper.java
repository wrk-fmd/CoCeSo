package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.unit.UnitBriefDto;
import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.coceso.dto.unit.UnitStateDto;
import at.wrk.coceso.dto.unit.UnitTypeDto;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.enums.UnitType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TaskMapper.class, StaffMapper.class})
public interface UnitMapper {

    UnitBriefDto unitToBriefDto(Unit unit);

    @Mapping(target = "concern", source = "concern.id")
    UnitDto unitToDto(Unit unit);

    UnitState stateDtoToState(UnitStateDto state);

    Set<UnitType> typeDtosToTypes(Collection<UnitTypeDto> dtos);

    default String typesToString(Collection<UnitType> types) {
        return types != null && !types.isEmpty() ? types.stream().map(UnitType::toString).collect(Collectors.joining(", ")) : null;
    }
}
