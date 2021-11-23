package at.wrk.coceso.service;

import at.wrk.coceso.dto.unit.UnitBatchCreateDto;
import at.wrk.coceso.dto.unit.UnitBriefDto;
import at.wrk.coceso.dto.unit.UnitCreateDto;
import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.coceso.dto.unit.UnitUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.StaffMember;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;

import java.util.List;

public interface UnitService {

    List<UnitDto> getAll(Concern concern);

    List<Unit> getAllSorted(Concern concern);

    List<Task> getRelated(Incident incident);

    UnitBriefDto create(Concern concern, UnitCreateDto data);

    List<UnitBriefDto> createBatch(Concern concern, UnitBatchCreateDto batch);

    void update(Unit unit, UnitUpdateDto data);

    void remove(Unit unit);

    void addCrewMember(Unit unit, StaffMember member);

    void removeCrewMember(Unit unit, StaffMember member);
}
