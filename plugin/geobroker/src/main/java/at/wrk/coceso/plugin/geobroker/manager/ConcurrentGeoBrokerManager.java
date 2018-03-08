package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.external.TargetPointExtractor;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerIncidentPublisher;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerUnitPublisher;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@ThreadSafe
@Component
@Scope(value = "singleton")
public class ConcurrentGeoBrokerManager implements GeoBrokerManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentGeoBrokerManager.class);

    private final Object updateLock;
    private final Map<String, GeoBrokerUnit> unitCache;
    private final Map<String, GeoBrokerIncident> incidentCache;

    private final GeoBrokerUnitPublisher unitPublisher;
    private final GeoBrokerIncidentPublisher incidentPublisher;
    private final TargetPointExtractor targetPointExtractor;

    @Autowired
    public ConcurrentGeoBrokerManager(
            final GeoBrokerUnitPublisher unitPublisher,
            final GeoBrokerIncidentPublisher incidentPublisher,
            final TargetPointExtractor targetPointExtractor) {
        this.unitPublisher = unitPublisher;
        this.incidentPublisher = incidentPublisher;
        this.targetPointExtractor = targetPointExtractor;

        this.updateLock = new Object();
        this.unitCache = new ConcurrentHashMap<>();
        this.incidentCache = new ConcurrentHashMap<>();
    }

    @Override
    public void unitUpdated(final GeoBrokerUnit unit) {
        synchronized (updateLock) {
            unitCache.put(unit.getId(), unit);
        }

        unitPublisher.unitUpdated(unit);
    }

    @Override
    public void unitDeleted(final String externalUnitId) {
        this.unitCache.remove(externalUnitId);
        unitPublisher.unitDeleted(externalUnitId);
    }

    @Override
    public void incidentUpdated(final GeoBrokerIncident incident) {
        Map<String, GeoBrokerUnit> updatedUnits;
        synchronized (updateLock) {
            incidentCache.put(incident.getId(), incident);
            updatedUnits = getUpdatedUnitEntitiesForAllReferencedUnits(incident);
            unitCache.putAll(updatedUnits);
        }

        updatedUnits.values().forEach(unitPublisher::unitUpdated);
        incidentPublisher.incidentUpdated(incident);
    }

    @Override
    public void incidentDeleted(final String externalIncidentId) {
        this.incidentCache.remove(externalIncidentId);
        incidentPublisher.incidentDeleted(externalIncidentId);
    }

    private Map<String, GeoBrokerUnit> getUpdatedUnitEntitiesForAllReferencedUnits(final GeoBrokerIncident incident) {
        Map<String, GeoBrokerUnit> updatedUnits;Set<GeoBrokerUnit> affectedUnits = incident.getAssignedExternalUnitIds()
                .stream()
                .map(unitCache::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        updatedUnits = affectedUnits
                .stream()
                .map(this::updateCachedUnit)
                .collect(Collectors.toMap(GeoBrokerUnit::getId, Function.identity()));
        return updatedUnits;
    }

    private GeoBrokerUnit updateCachedUnit(final GeoBrokerUnit unit) {
        List<String> assignedUnits = unit.getIncidents()
                .stream()
                .flatMap(
                        incidentId -> Optional.ofNullable(incidentCache.get(incidentId))
                                .map(GeoBrokerIncident::getAssignedExternalUnitIds)
                                .orElse(ImmutableList.of())
                                .stream())
                .collect(Collectors.toList());

        GeoBrokerPoint targetPoint = getTargetPointForUnit(unit);

        return new GeoBrokerUnit(
                unit.getId(),
                unit.getName(),
                unit.getToken(),
                assignedUnits,
                unit.getIncidentsWithState(),
                unit.getLastPoint(),
                targetPoint);
    }

    private GeoBrokerPoint getTargetPointForUnit(final GeoBrokerUnit unit) {
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

        GeoBrokerIncident incident = incidentCache.get(externalIncidentKey);

        if (incident == null) {
            LOG.warn("Referenced Incident with id {} is not present in cache.", externalIncidentKey);
        } else {
            targetPoint = targetPointExtractor.getTargetPoint(incident, taskState);
        }

        return targetPoint;
    }
}
