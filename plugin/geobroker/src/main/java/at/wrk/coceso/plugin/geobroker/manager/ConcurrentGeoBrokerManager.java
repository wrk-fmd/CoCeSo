package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import at.wrk.coceso.plugin.geobroker.external.TargetPointExtractor;
import at.wrk.coceso.plugin.geobroker.filter.IncidentFilter;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerIncidentPublisher;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerUnitPublisher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ThreadSafe
@Component
@Scope(value = "singleton")
public class ConcurrentGeoBrokerManager implements GeoBrokerManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentGeoBrokerManager.class);

    private final Object updateLock;
    private final Map<String, CachedUnit> unitCache;
    private final Map<String, CachedIncident> incidentCache;

    private final GeoBrokerUnitPublisher unitPublisher;
    private final GeoBrokerIncidentPublisher incidentPublisher;
    private final TargetPointExtractor targetPointExtractor;
    private final boolean showAllUnitsForOfficers;
    private IncidentFilter incidentFilter;

    @Autowired
    public ConcurrentGeoBrokerManager(
            final GeoBrokerUnitPublisher unitPublisher,
            final GeoBrokerIncidentPublisher incidentPublisher,
            final TargetPointExtractor targetPointExtractor,
            @Value("${geobroker.all.units.for.officers:false}") final boolean showAllUnitsForOfficers,
            final IncidentFilter incidentFilter) {
        this.unitPublisher = unitPublisher;
        this.incidentPublisher = incidentPublisher;
        this.targetPointExtractor = targetPointExtractor;
        this.showAllUnitsForOfficers = showAllUnitsForOfficers;
        this.incidentFilter = incidentFilter;

        this.updateLock = new Object();
        this.unitCache = new ConcurrentHashMap<>();
        this.incidentCache = new ConcurrentHashMap<>();
    }

    @Override
    public void unitUpdated(final CachedUnit unit) {
        final boolean wasAdded;

        CachedUnit updatedUnit;
        synchronized (updateLock) {
            updatedUnit = updateCachedUnit(unit);
            CachedUnit previousValue = unitCache.put(updatedUnit.getId(), updatedUnit);
            wasAdded = previousValue == null;
        }

        unitPublisher.unitUpdated(updatedUnit.getUnit());
        if (wasAdded) {
            updateAllOfficerUnits(unit.getConcernId());
        }
    }

    @Override
    public void unitDeleted(final String externalUnitId) {
        this.unitCache.remove(externalUnitId);
        unitPublisher.unitDeleted(externalUnitId);
    }

    @Override
    public void incidentUpdated(final CachedIncident incident) {
        Map<String, CachedUnit> updatedUnits;
        synchronized (updateLock) {
            incidentCache.put(incident.getId(), incident);
            updatedUnits = getUpdatedUnitEntitiesForAllReferencedUnits(incident);
            unitCache.putAll(updatedUnits);
        }


        publishUpdatedUnits(updatedUnits.values());
        publishIncident(incident);
        updateAllOfficerUnits(incident.getConcernId());
    }

    private void publishIncident(final CachedIncident incident) {
        if (incidentFilter.isIncidentRelevantForGeoBroker(incident)) {
            incidentPublisher.incidentUpdated(incident.getIncident());
        } else {
            LOG.debug("Incident is not supported for publishing: {} of type {}", incident.getId(), incident.getIncidentType());
            incidentPublisher.incidentDeleted(incident.getId());
        }
    }

    @Override
    public void incidentDeleted(final String externalIncidentId) {
        this.incidentCache.remove(externalIncidentId);
        incidentPublisher.incidentDeleted(externalIncidentId);
    }

    private void updateAllOfficerUnits(final int concernId) {
        Set<CachedUnit> updatedOfficerUnits;
        updatedOfficerUnits = getAllUpdatedOfficerUnits(concernId);
        publishUpdatedUnits(updatedOfficerUnits);
    }

    private void publishUpdatedUnits(final Collection<CachedUnit> cachedUnits) {
        cachedUnits
                .stream()
                .map(CachedUnit::getUnit)
                .forEach(unitPublisher::unitUpdated);
    }

    private Set<CachedUnit> getAllUpdatedOfficerUnits(final int concernId) {
        return unitCache.values()
                .stream()
                .filter(cachedUnit -> Objects.equals(cachedUnit.getUnitType(), UnitType.Officer))
                .filter(cachedUnit -> cachedUnit.getConcernId() == concernId)
                .map(this::updateCachedUnit)
                .collect(Collectors.toSet());
    }

    private Map<String, CachedUnit> getUpdatedUnitEntitiesForAllReferencedUnits(final CachedIncident incident) {
        return incident.getAssignedExternalUnitIds()
                .keySet()
                .stream()
                .distinct()
                .map(unitCache::get)
                .filter(Objects::nonNull)
                .distinct()
                .map(this::updateCachedUnit)
                .collect(Collectors.toMap(CachedUnit::getId, Function.identity()));
    }

    private CachedUnit updateCachedUnit(final CachedUnit unit) {
        List<String> assignedUnits;
        List<String> assignedIncidents;

        if (showAllUnitsForOfficers && Objects.equals(unit.getUnitType(), UnitType.Officer)) {
            // Providing all units and incidents of the same concern for Officer-Unit.
            assignedUnits = getAllExternalUnitIdsForConcernId(unit.getConcernId());
            assignedIncidents = getAllExternalIncidentIdsForConcernId(unit.getConcernId());
        } else {
            // Providing only assigned units and incidents to standard unit.
            assignedUnits = getAssignedUnitsForStandardUnit(unit);
            assignedIncidents = ImmutableList.copyOf(unit.getIncidentsWithState().keySet());
        }

        GeoBrokerPoint targetPoint = getTargetPointForUnit(unit);

        GeoBrokerUnit updatedGeoBrokerUnit = updateGeoBrokerUnit(unit, assignedUnits, assignedIncidents, targetPoint);
        return new CachedUnit(updatedGeoBrokerUnit, unit.getIncidentsWithState(), unit.getUnitType(), unit.getConcernId());
    }

    private GeoBrokerUnit updateGeoBrokerUnit(
            final CachedUnit existingUnit,
            final List<String> assignedUnits,
            final List<String> incidents,
            final GeoBrokerPoint targetPoint) {
        GeoBrokerUnit geoBrokerUnit = existingUnit.getUnit();
        return new GeoBrokerUnit(
                geoBrokerUnit.getId(),
                geoBrokerUnit.getName(),
                geoBrokerUnit.getToken(),
                assignedUnits,
                incidents,
                geoBrokerUnit.getLastPoint(),
                targetPoint);
    }

    private List<String> getAllExternalUnitIdsForConcernId(final int concernId) {
        return unitCache.values()
                .stream()
                .filter(cachedUnit -> cachedUnit.getConcernId() == concernId)
                .map(CachedUnit::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getAllExternalIncidentIdsForConcernId(final int concernId) {
        return incidentCache.values()
                .stream()
                .filter(cachedIncident -> cachedIncident.getConcernId() == concernId)
                .map(CachedIncident::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getAssignedUnitsForStandardUnit(final CachedUnit unit) {
        List<String> assignedUnits;
        assignedUnits = unit.getIncidentsWithState()
                .keySet()
                .stream()
                .flatMap(this::getAllAssignedUnitsOfIncident)
                .distinct()
                .collect(Collectors.toList());
        return assignedUnits;
    }

    private Stream<String> getAllAssignedUnitsOfIncident(final String incidentId) {
        return Optional.ofNullable(incidentCache.get(incidentId))
                .map(CachedIncident::getAssignedExternalUnitIds)
                .map(Map::keySet)
                .orElse(ImmutableSet.of())
                .stream();
    }

    private GeoBrokerPoint getTargetPointForUnit(final CachedUnit unit) {
        List<GeoBrokerPoint> possibleTargetPoints = unit.getIncidentsWithState()
                .entrySet()
                .stream()
                .map(entry -> getPointForIncident(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        return possibleTargetPoints.size() == 1 ? possibleTargetPoints.get(0) : null;
    }

    @Nullable
    private GeoBrokerPoint getPointForIncident(final String externalIncidentKey, final TaskState taskState) {
        GeoBrokerPoint targetPoint = null;

        CachedIncident incident = incidentCache.get(externalIncidentKey);

        if (incident == null) {
            LOG.warn("Referenced Incident with id {} is not present in cache.", externalIncidentKey);
        } else {
            targetPoint = targetPointExtractor.getTargetPoint(incident, taskState);
        }

        return targetPoint;
    }
}
