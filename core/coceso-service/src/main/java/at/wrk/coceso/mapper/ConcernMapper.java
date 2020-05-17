package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.concern.ConcernBriefDto;
import at.wrk.coceso.dto.concern.ConcernDto;
import at.wrk.coceso.entity.Concern;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConcernMapper {

    ConcernBriefDto concernToBriefDto(Concern concern);

    ConcernDto concernToDto(Concern concern);
}
