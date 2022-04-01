package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.auth.AuthorizationProvider;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.AccessLevel;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.entityevent.impl.NotifyList;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.form.Group;
import at.wrk.coceso.repository.PatientRepository;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.patadmin.internal.PatadminServiceInternal;
import at.wrk.coceso.specification.PatientSearchSpecification;
import at.wrk.coceso.utils.AuthenticatedUserProvider;
import at.wrk.coceso.utils.DataAccessLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
class PatadminServiceImpl implements PatadminServiceInternal {

    private final static Logger LOG = LoggerFactory.getLogger(PatadminServiceImpl.class);

    @Autowired
    private AuthorizationProvider auth;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private LogService logService;

    private final DataAccessLogger dataAccessLogger;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    PatadminServiceImpl(final DataAccessLogger dataAccessLogger, final AuthenticatedUserProvider authenticatedUserProvider) {
        this.dataAccessLogger = dataAccessLogger;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    public boolean[] getAccessLevels(final Concern concern) {
        return new boolean[]{
                auth.hasPermission(concern, AccessLevel.PatadminSettings),
                auth.hasPermission(concern, AccessLevel.PatadminRegistration),
                auth.hasPermission(concern, AccessLevel.PatadminPostprocessing),
                auth.hasPermission(concern, AccessLevel.PatadminInfo)
        };
    }

    @Override
    public void addAccessLevels(final ModelMap map, final Concern concern) {
        map.addAttribute("accessLevels", getAccessLevels(concern));
    }

    @Override
    public List<Patient> getAllInTreatment(final Concern concern) {
        List<Patient> patients = patientRepository.findInTreatment(concern);
        dataAccessLogger.logPatientAccess(patients, concern);
        return patients;
    }

    @Override
    public List<Patient> getPatientsByQuery(Concern concern, String query, boolean showDone) {
        query = query.trim();
        if (query.length() < 1) {
            return Collections.emptyList();
        }

        List<Patient> patients = patientRepository.findAll(new PatientSearchSpecification(query, concern, showDone));
        dataAccessLogger.logPatientAccess(patients, concern);
        return patients;
    }

    @Override
    public List<Unit> getGroups(final Concern concern) {
        List<Unit> groups = unitRepository.findByConcernAndTypeIn(concern, UnitType.treatmentTypes);
        Collections.sort(groups);
        return groups;
    }

    @Override
    public Unit getGroup(final int id) {
        Unit group = unitRepository.getById(id);
        if (group == null) {
            throw new ErrorsException(Errors.EntityMissing);
        }

        if (group.getConcern().isClosed()) {
            throw new ErrorsException(Errors.ConcernClosed);
        }

        if (!group.getType().isTreatment()) {
            throw new ErrorsException(Errors.NotTreatment);
        }

        return group;
    }

    @Override
    public List<Unit> update(final List<Group> groups, final Concern concern, final NotifyList notify) {
        Set<Unit> save = groups.stream()
                .map(group -> updateTreatmentGroup(concern, notify, group))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        return unitRepository.saveAllAndFlush(save);
    }

    private Unit updateTreatmentGroup(final Concern concern, final NotifyList notify, final Group group) {
        Unit unit = getGroup(group.getId());
        Unit unitAfterUpdate;
        if (!unit.getConcern().equals(concern)) {
            LOG.warn("{}: Tried to update group {} of wrong concern", authenticatedUserProvider.getAuthenticatedUser(), unit);
            unitAfterUpdate = null;
        } else {
            Changes changes = new Changes("unit");

            if (group.isActive() != (unit.getState() == UnitState.EB)) {
                // Group active is equivalent to UnitState EB in database
                UnitState state = group.isActive() ? UnitState.EB : UnitState.NEB;
                changes.put("state", unit.getState(), state);
                unit.setState(state);
            }

            if (!Objects.equals(group.getCapacity(), unit.getCapacity())) {
                changes.put("capacity", unit.getCapacity(), group.getCapacity());
                unit.setCapacity(group.getCapacity());
            }

            String imgsrc = StringUtils.trimToNull(group.getImgsrc());
            if (!Objects.equals(imgsrc, unit.getImgsrc())) {
                changes.put("imgsrc", unit.getImgsrc(), imgsrc);
                unit.setImgsrc(imgsrc);
            }

            if (!changes.isEmpty()) {
                LOG.debug("{}: Triggered update of unit #{} with changes {}.", authenticatedUserProvider.getAuthenticatedUser(), unit.getId(), changes);
                logService.logAuto(LogEntryType.UNIT_UPDATE, unit.getConcern(), unit, null, changes);
                notify.addUnit(unit.getId());
                unitAfterUpdate = unit;
            } else {
                LOG.debug(
                        "{}: Triggered update of unit #{} (Treatment group) without any effective changes. Update is skipped.",
                        authenticatedUserProvider.getAuthenticatedUser(),
                        unit.getId());
                unitAfterUpdate = null;
            }
        }

        return unitAfterUpdate;
    }
}
