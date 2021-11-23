package at.wrk.coceso.service;

import at.wrk.coceso.dto.concern.ConcernBriefDto;
import at.wrk.coceso.dto.concern.ConcernCreateDto;
import at.wrk.coceso.dto.concern.ConcernDto;
import at.wrk.coceso.dto.concern.ConcernUpdateDto;
import at.wrk.coceso.dto.concern.SectionCreateDto;
import at.wrk.coceso.entity.Concern;

import java.util.List;
import java.util.Optional;

public interface ConcernService {

    Optional<Concern> getConcern(long id);

    ConcernDto getConcern(Concern concern);

    List<ConcernBriefDto> getAllBrief();

    List<ConcernDto> getAll();

    List<Concern> getAllOpen();

    Concern getByName(String name);

    ConcernBriefDto create(ConcernCreateDto data);

    void update(Concern concern, ConcernUpdateDto data);

    void setClosed(Concern concern, boolean close);

    void addSection(Concern concern, SectionCreateDto data);

    void removeSection(Concern concern, String section);
}
