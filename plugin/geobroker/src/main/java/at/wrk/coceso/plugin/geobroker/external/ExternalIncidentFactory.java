package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static at.wrk.coceso.plugin.geobroker.external.GeoBrokerPoints.mapPoint;

@Component
@PropertySource(value = "classpath:geobroker.properties", ignoreResourceNotFound = true)
public class ExternalIncidentFactory implements GeoBrokerIncidentFactory {

    private final ExternalIncidentIdGenerator incidentIdGenerator;
    private final ExternalUnitIdGenerator unitIdGenerator;
    private final boolean exposeInfoField;

    @Autowired
    public ExternalIncidentFactory(
            final ExternalIncidentIdGenerator incidentIdGenerator,
            final ExternalUnitIdGenerator unitIdGenerator,
            @Value("${geobroker.expose.info.field:false}") final boolean exposeInfoField) {
        this.incidentIdGenerator = incidentIdGenerator;
        this.unitIdGenerator = unitIdGenerator;
        this.exposeInfoField = exposeInfoField;
    }

    @Override
    public CachedIncident createExternalIncident(final Incident incident) {
        Integer concernId = incident.getConcern().getId();
        String externalIncidentId = incidentIdGenerator.generateExternalIncidentId(incident.getId(), concernId);

        List<String> assignedUnitIds = incident.getUnitsSlim()
                .keySet()
                .stream()
                .map(unitId -> unitIdGenerator.generateExternalUnitId(unitId, concernId))
                .collect(Collectors.toList());

        GeoBrokerIncident geoBrokerIncident = new GeoBrokerIncident(
                externalIncidentId,
                incident.getType().name(),
                incident.isPriority(),
                incident.isBlue(),
                exposeInfoField ? incident.getInfo() : "-",
                mapPoint(incident.getBo()));
        return new CachedIncident(geoBrokerIncident, assignedUnitIds, mapPoint(incident.getAo()), concernId, incident.getType(), incident.getState());
    }
}
