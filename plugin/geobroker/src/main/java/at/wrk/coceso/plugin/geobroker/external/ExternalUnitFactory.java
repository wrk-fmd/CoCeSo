package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static at.wrk.coceso.plugin.geobroker.external.GeoBrokerPoints.mapPoint;
import static java.util.stream.Collectors.toMap;

@Component
public class ExternalUnitFactory implements GeoBrokerUnitFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalUnitFactory.class);

    private final ExternalUnitIdGenerator unitIdGenerator;
    private final ExternalUnitTokenGenerator tokenGenerator;
    private final ExternalIncidentIdGenerator incidentIdGenerator;

    @Autowired
    public ExternalUnitFactory(
            final ExternalUnitIdGenerator unitIdGenerator,
            final ExternalUnitTokenGenerator tokenGenerator,
            final ExternalIncidentIdGenerator incidentIdGenerator) {
        this.unitIdGenerator = unitIdGenerator;
        this.tokenGenerator = tokenGenerator;
        this.incidentIdGenerator = incidentIdGenerator;
    }

    @Override
    public GeoBrokerUnit createExternalUnit(final Unit unit) {
        Integer concernId = unit.getConcern().getId();
        LOG.trace(
                "Creating GeoBrokerUnit for Unit: unitId={}, concernId={}, assignedIncidents={}",
                unit.getId(),
                concernId,
                unit.getIncidentsSlim());
        String externalId = getExternalUnitId(unit);
        String token = tokenGenerator.generateToken(unit);

        Map<String, TaskState> externalIncidentIds = mapToExternalIncidentIds(concernId, unit.getIncidents());

        // Target Point and referenced Units are caluculated in GeoBrokerManager.
        return new GeoBrokerUnit(
                externalId,
                Optional.ofNullable(unit.getCall()).orElse(""),
                token,
                ImmutableList.of(),
                externalIncidentIds,
                mapPoint(unit.getPosition()),
                null);
    }

    private String getExternalUnitId(final Unit unit) {
        return unitIdGenerator.generateExternalUnitId(unit.getId(), unit.getConcern().getId());
    }

    private Map<String, TaskState> mapToExternalIncidentIds(final int concernId, final Map<Incident, TaskState> assignedIncidents) {
        return Optional.ofNullable(assignedIncidents)
                .orElseGet(this::emptyMapWithWarning)
                .entrySet()
                .stream()
                .collect(toMap(x -> incidentIdGenerator.generateExternalIncidentId(x.getKey().getId(), concernId), Map.Entry::getValue));
    }

    private Map<Incident, TaskState> emptyMapWithWarning() {
        LOG.warn("Assigned incidents for unit are null.");
        return ImmutableMap.of();
    }
}
