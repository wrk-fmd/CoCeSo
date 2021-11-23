package at.wrk.coceso.service;

import at.wrk.coceso.dto.journal.CustomJournalEntryDto;
import at.wrk.coceso.dto.journal.JournalEntryDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.JournalEntry;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.journal.ChangesCollector;

import java.time.Instant;
import java.util.List;

public interface JournalService {

    void logConcern(JournalEntryType type, Concern concern, ChangesCollector changes);

    void logIncident(JournalEntryType type, Incident incident, ChangesCollector changes);

    void logUnit(JournalEntryType type, Unit unit, ChangesCollector changes);

    void logTask(JournalEntryType type, Task task);

    void logTaskDetach(JournalEntryType type, Task task);

    void logPatient(JournalEntryType type, Patient patient, ChangesCollector changes);

    void logPatientAssign(JournalEntryType type, Incident incident);

    void logCustom(Concern concern, CustomJournalEntryDto data);

    List<JournalEntry> getAll(Concern concern);

    List<JournalEntryDto> getLast(Concern concern, int count);

    List<JournalEntryDto> getByIncident(Incident incident, Integer limit);

    List<JournalEntry> getByIncidentAsc(Incident incident);

    List<JournalEntryDto> getByUnit(Unit unit, Integer limit);

    List<JournalEntry> getByUnitAsc(Unit unit);

    List<JournalEntry> getByPatient(Patient patient);

    List<JournalEntry> getStatesByPatient(Patient patient);

    List<JournalEntry> getByIncidentAndUnit(Incident incident, Unit unit);

    Instant getLastTaskStateUpdate(Incident incident, Unit unit);

    List<JournalEntryDto> getCustom(Concern concern);

    List<JournalEntry> getCustomAsc(Concern concern);

    void updateForRemoval(Unit unit);
}
