package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static at.wrk.coceso.plugin.geobroker.external.GeoBrokerPoints.mapPoint;

@Component
public class ExternalIncidentFactory implements GeoBrokerIncidentFactory {

    private final ExternalIncidentIdGenerator incidentIdGenerator;
    private final ExternalUnitIdGenerator unitIdGenerator;

    @Autowired
    public ExternalIncidentFactory(
            final ExternalIncidentIdGenerator incidentIdGenerator,
            final ExternalUnitIdGenerator unitIdGenerator) {
        this.incidentIdGenerator = incidentIdGenerator;
        this.unitIdGenerator = unitIdGenerator;
    }

    @Override
    public GeoBrokerIncident createExternalIncident(final Incident incident) {
        Integer concernId = incident.getConcern().getId();
        String externalIncidentId = incidentIdGenerator.generateExternalIncidentId(incident.getId(), concernId);

        List<String> assignedUnitIds = incident.getUnitsSlim()
                .keySet()
                .stream()
                .map(unitId -> unitIdGenerator.generateExternalUnitId(unitId, concernId))
                .collect(Collectors.toList());

        return new GeoBrokerIncident(
                externalIncidentId,
                incident.getType().name(),
                incident.isPriority(),
                incident.isBlue(),
                "",
                mapPoint(incident.getBo()),
                assignedUnitIds,
                mapPoint(incident.getAo()));
    }
}
