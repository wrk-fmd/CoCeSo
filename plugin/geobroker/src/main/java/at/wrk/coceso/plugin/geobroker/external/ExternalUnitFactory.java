package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import at.wrk.geocode.LatLng;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class ExternalUnitFactory implements GeoBrokerUnitFactory {
    private final ExternalUnitIdGenerator unitIdGenerator;
    private final ExternalUnitTokenGenerator tokenGenerator;
    private final ExternalIncidentIdGenerator incidentIdGenerator;
    private final TargetPointExtractor targetPointExtractor;

    @Autowired
    public ExternalUnitFactory(
            final ExternalUnitIdGenerator unitIdGenerator,
            final ExternalUnitTokenGenerator tokenGenerator,
            final ExternalIncidentIdGenerator incidentIdGenerator,
            final TargetPointExtractor targetPointExtractor) {
        this.unitIdGenerator = unitIdGenerator;
        this.tokenGenerator = tokenGenerator;
        this.incidentIdGenerator = incidentIdGenerator;
        this.targetPointExtractor = targetPointExtractor;
    }

    @Override
    public GeoBrokerUnit createExternalUnit(final Unit unit) {
        String externalId = unitIdGenerator.generateExternalUnitId(unit.getId(), unit.getConcern().getId());
        String token = tokenGenerator.generateToken(unit);

        Set<Incident> assignedIncidents = Optional.ofNullable(unit.getIncidents())
                .map(Map::keySet)
                .orElse(ImmutableSet.of());

        GeoBrokerPoint targetPoint = getTargetPointIfOneTargetIsPresent(unit, assignedIncidents);

        List<String> externalIncidentIds = assignedIncidents
                .stream()
                .map(incidentIdGenerator::generateExternalIncidentId)
                .distinct()
                .collect(Collectors.toList());

        return new GeoBrokerUnit(
                externalId,
                Optional.ofNullable(unit.getCall()).orElse(""),
                token,
                ImmutableList.of(),
                externalIncidentIds,
                mapPoint(unit.getPosition()),
                targetPoint);
    }

    @Nullable
    private GeoBrokerPoint getTargetPointIfOneTargetIsPresent(final Unit unit, final Set<Incident> assignedIncidents) {
        List<GeoBrokerPoint> targetPoints = assignedIncidents
                .stream()
                .map(incident -> targetPointExtractor.getTargetPoint(incident, unit.getIncidents().get(incident)))
                .filter(Objects::nonNull)
                .map(this::mapPoint)
                .distinct()
                .collect(toList());

        return targetPoints.size() == 1 ? targetPoints.get(0) : null;
    }

    private GeoBrokerPoint mapPoint(@Nullable final Point position) {
        GeoBrokerPoint point = null;

        if (position != null) {
            LatLng coordinates = position.getCoordinates();
            point = new GeoBrokerPoint(coordinates.getLat(), coordinates.getLng());
        }

        return point;
    }
}
