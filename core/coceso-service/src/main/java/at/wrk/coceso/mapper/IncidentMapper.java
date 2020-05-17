package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.incident.IncidentBriefDto;
import at.wrk.coceso.dto.incident.IncidentClosedReasonDto;
import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.dto.incident.IncidentTypeDto;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentClosedReason;
import at.wrk.coceso.entity.enums.IncidentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface IncidentMapper {

    IncidentBriefDto incidentToBriefDto(Incident incident);

    @Mapping(target = "concern", source = "concern.id")
    @Mapping(target = "patient", source = "patient.id")
    IncidentDto incidentToDto(Incident incident);

    IncidentType typeDtoToType(IncidentTypeDto type);

    @ValueMapping(source = "Open", target = MappingConstants.NULL)
    IncidentClosedReason closedReasonDtoClosedReason(IncidentClosedReasonDto state);
}
