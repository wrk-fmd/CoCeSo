package at.wrk.coceso.service;

import at.wrk.coceso.dto.incident.IncidentBriefDto;
import at.wrk.coceso.dto.incident.IncidentCreateDto;
import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.dto.incident.IncidentUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;

import java.util.List;
import java.util.Map;

public interface IncidentService {

    List<Incident> getAll(Concern concern);

    List<Incident> getAllSorted(Concern concern);

    List<IncidentDto> getAllRelevant(Concern concern);

    List<Incident> getAllForReport(Concern concern);

    List<Incident> getAllForDump(Concern concern);

    List<Incident> getAllTransports(Concern concern);

    List<Incident> getAllActive(Concern concern);

    Map<Incident, TaskState> getRelated(Unit unit);

    IncidentBriefDto create(Concern concern, IncidentCreateDto data);

    void update(Incident incident, IncidentUpdateDto data);

    void assignPatient(Incident incident, Patient patient);
}
