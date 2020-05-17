package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.unit.UnitBatchCreateDto;
import at.wrk.coceso.dto.unit.UnitBriefDto;
import at.wrk.coceso.dto.unit.UnitCreateDto;
import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.coceso.dto.unit.UnitUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Contact;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.StaffMember;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.entity.journal.ChangesCollector;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.event.events.ContainerEvent;
import at.wrk.coceso.event.events.UnitEvent;
import at.wrk.coceso.mapper.StaffMapper;
import at.wrk.coceso.mapper.UnitMapper;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.ContainerService;
import at.wrk.coceso.service.JournalService;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.utils.AuthenticatedUser;
import at.wrk.fmd.mls.event.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;
    private final StaffMapper staffMapper;

    private final ContainerService containerService;
    private final PointService pointService;
    private final JournalService journalService;

    private final EventBus eventBus;

    @Autowired
    public UnitServiceImpl(final UnitRepository unitRepository, final UnitMapper unitMapper, final StaffMapper staffMapper,
            final ContainerService containerService, final PointService pointService, final JournalService journalService,
            final EventBus eventBus) {
        this.unitRepository = unitRepository;
        this.unitMapper = unitMapper;
        this.staffMapper = staffMapper;
        this.containerService = containerService;
        this.pointService = pointService;
        this.journalService = journalService;
        this.eventBus = eventBus;
    }

//    @Override
//    public Unit getTreatmentByCall(String call, Concern concern) {
//        return (call == null || concern == null) ? null
//                : unitRepository.findFirstByCallIgnoreCaseAndConcernAndTypeIn(call, concern, UnitType.treatmentTypes);
//    }

    @Override
    public List<UnitDto> getAll(Concern concern) {
        return unitRepository.findByConcern(concern).stream()
                .map(unitMapper::unitToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Unit> getAllSorted(Concern concern) {
        return unitRepository.findByConcern(concern, Sort.by(Sort.Direction.ASC, "call"));
    }

    @Override
    public List<Task> getRelated(Incident incident) {
        return unitRepository.findByIdIn(unitRepository.findRelated(incident)).stream()
                .map(u -> u.getTask(incident).orElseGet(() -> new Task(incident, u, TaskState.Detached)))
                .collect(Collectors.toList());
    }

    @Override
    public UnitBriefDto create(final Concern concern, final UnitCreateDto data) {
        log.debug("{}: Creating unit: '{}'", AuthenticatedUser.getName(), data);

        Unit unit = new Unit();
        ChangesCollector changes = new ChangesCollector("unit");

        // Set properties
        unit.setConcern(concern);

        changes.put("call", data.getCall());
        unit.setCall(data.getCall());

        Set<UnitType> types = unitMapper.typeDtosToTypes(data.getTypes());
        if (types != null && !types.isEmpty()) {
            changes.put("types", unitMapper.typesToString(types));
            unit.setTypes(types);
        }

        if (data.getInfo() != null) {
            changes.put("info", data.getInfo());
            unit.setInfo(data.getInfo());
        }

        // Using null for the concern prevents a UnitPoint being created. Maybe make that more explicit?
        Point home = pointService.getPoint(null, data.getHome());
        if (!Point.isEmpty(home)) {
            changes.put("home", Point.toStringOrNull(home));
            unit.setHome(home);
        }

        if (data.isPortable()) {
            changes.put("portable", data.isPortable());
            unit.setPortable(data.isPortable());
        }

        Set<Contact> contacts = staffMapper.contactDtosToContacts(data.getContacts());
        if (contacts != null && !contacts.isEmpty()) {
            changes.put("contacts", staffMapper.contactsToString(contacts));
            unit.setContacts(contacts);
        }

        if (data.getSection() != null && !data.getSection().isEmpty()) {
            if (concern.containsSection(data.getSection())) {
                changes.put("section", data.getSection());
                unit.setSection(data.getSection());
            } else {
                log.info("Tried to create unit with unknown section: '{}'", data.getSection());
            }
        }

        unit = unitRepository.save(unit);
        journalService.logUnit(JournalEntryType.UNIT_CREATE, unit, changes);
        eventBus.publish(new UnitEvent(unitMapper.unitToDto(unit)));

        // Notify the hierarchical view about the new unit
        eventBus.publish(new ContainerEvent(containerService.getRoot(concern)));

        return unitMapper.unitToBriefDto(unit);
    }

    @Override
    public List<UnitBriefDto> createBatch(final Concern concern, final UnitBatchCreateDto batch) {
        List<UnitBriefDto> created = new LinkedList<>();

        UnitCreateDto unit = new UnitCreateDto();
        unit.setPortable(batch.isPortable());
        unit.setHome(batch.getHome());

        for (int i = batch.getFrom(); i <= batch.getTo(); i++) {
            unit.setCall(batch.getCall() + i);
            created.add(create(concern, unit));
        }

        return created;
    }

    @Override
    public void update(final Unit unit, final UnitUpdateDto data) {
        log.debug("{}: Updating unit '{}' to '{}'", AuthenticatedUser.getName(), unit, data);

        // Set updateable properties
        ChangesCollector changes = new ChangesCollector("unit");

        if (data.getCall() != null && !data.getCall().equals(unit.getCall())) {
            changes.put("call", unit.getCall(), data.getCall());
            unit.setCall(data.getCall());
        }

        Set<UnitType> types = unitMapper.typeDtosToTypes(data.getTypes());
        if (types != null && !types.equals(unit.getTypes())) {
            changes.put("types", unitMapper.typesToString(unit.getTypes()), unitMapper.typesToString(types));
            unit.setTypes(types);
        }

        UnitState state = unitMapper.stateDtoToState(data.getState());
        if (state != null && state != unit.getState()) {
            changes.put("state", unit.getState(), state);
            unit.setState(state);
        }

        if (data.getPortable() != null && !data.getPortable().equals(unit.isPortable())) {
            changes.put("portable", unit.isPortable(), data.getPortable());
            unit.setPortable(data.getPortable());
        }

        if (data.getInfo() != null && !data.getInfo().equals(unit.getInfo())) {
            changes.put("info", data.getInfo());
            unit.setInfo(data.getInfo());
        }

        // Using null for the concern prevents a UnitPoint being created. Maybe make that more explicit?
        Point home = pointService.getPoint(null, data.getHome());
        if (data.getHome() != null && !Point.infoEquals(home, unit.getHome())) {
            changes.put("home", Point.toStringOrNull(unit.getHome()), Point.toStringOrNull(home));
            unit.setHome(home);
        }

        Point position = pointService.getPoint(unit.getConcern(), data.getPosition());
        if (data.getPosition() != null && !Point.infoEquals(position, unit.getPosition())) {
            changes.put("position", Point.toStringOrNull(unit.getPosition()), Point.toStringOrNull(position));
            unit.setPosition(position);
        }

        Set<Contact> contacts = staffMapper.contactDtosToContacts(data.getContacts());
        if (contacts != null && !contacts.equals(unit.getContacts())) {
            changes.put("contacts", staffMapper.contactsToString(unit.getContacts()), staffMapper.contactsToString(contacts));
            unit.setContacts(contacts);
        }

        if (data.getSection() != null) {
            String section = data.getSection().isEmpty() ? null : data.getSection();
            if (!Objects.equals(section, unit.getSection())) {
                if (section == null || unit.getConcern().containsSection(data.getSection())) {
                    changes.put("section", unit.getSection(), section);
                    unit.setSection(section);
                } else {
                    log.info("Tried to set unknown section for unit: '{}'", section);
                }
            }
        }

        if (!changes.isEmpty()) {
            journalService.logUnit(JournalEntryType.UNIT_UPDATE, unit, changes);
            eventBus.publish(new UnitEvent(unitMapper.unitToDto(unit)));
        }
    }

    @Override
    public void remove(Unit unit) {
        // TODO
//        if (unit.isLocked()) {
//            log.warn("{}: Tried to remove non-deletable Unit #{}", authenicatedUserProvider.getAuthenticatedUser(), unit.getId());
//            throw new ErrorsException(Errors.UnitLocked);
//        }

        journalService.updateForRemoval(unit);
        unitRepository.delete(unit);

        // TODO Send notifications
    }

    @Override
    public void addCrewMember(Unit unit, StaffMember member) {
        // TODO Logging
        unit.addCrew(member);

        // TODO Specific event?
        eventBus.publish(new UnitEvent(unitMapper.unitToDto(unit)));
    }

    @Override
    public void removeCrewMember(Unit unit, StaffMember member) {
        // TODO Logging
        unit.removeCrew(member);

        // TODO Specific event?
        eventBus.publish(new UnitEvent(unitMapper.unitToDto(unit)));
    }
}
