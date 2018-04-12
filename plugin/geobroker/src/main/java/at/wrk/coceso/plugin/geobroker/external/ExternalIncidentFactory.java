package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static at.wrk.coceso.plugin.geobroker.external.GeoBrokerPoints.mapPoint;
import static java.util.stream.Collectors.toMap;

@Component
@PropertySource(value = "classpath:geobroker.properties", ignoreResourceNotFound = true)
public class ExternalIncidentFactory implements GeoBrokerIncidentFactory {

    private final ExternalIncidentIdGenerator incidentIdGenerator;
    private final ExternalUnitIdGenerator unitIdGenerator;
    private final boolean exposeInfoField;
    private boolean exposeBoField;

    @Autowired
    public ExternalIncidentFactory(
            final ExternalIncidentIdGenerator incidentIdGenerator,
            final ExternalUnitIdGenerator unitIdGenerator,
            @Value("${geobroker.expose.info.field:false}") final boolean exposeInfoField,
            @Value("${geobroker.expose.bo.field:false}") final boolean exposeBoField) {
        this.incidentIdGenerator = incidentIdGenerator;
        this.unitIdGenerator = unitIdGenerator;
        this.exposeInfoField = exposeInfoField;
        this.exposeBoField = exposeBoField;
    }

    @Override
    public CachedIncident createExternalIncident(final Incident incident) {
        Integer concernId = incident.getConcern().getId();
        String externalIncidentId = incidentIdGenerator.generateExternalIncidentId(incident.getId(), concernId);

        Map<String, TaskState> assignedUnitIds = incident.getUnitsSlim()
                .entrySet()
                .stream()
                .collect(toMap(entry -> unitIdGenerator.generateExternalUnitId(entry.getKey(), concernId), Map.Entry::getValue));

        GeoBrokerIncident geoBrokerIncident = new GeoBrokerIncident(
                externalIncidentId,
                incident.getType().name(),
                incident.isPriority(),
                incident.isBlue(),
                createGeoBrokerInfo(incident),
                mapPoint(incident.getBo()));
        return new CachedIncident(geoBrokerIncident, assignedUnitIds, mapPoint(incident.getAo()), concernId, incident.getType(), incident.getState());
    }

    private String createGeoBrokerInfo(final Incident incident) {
        List<String> informations = new ArrayList<>();

        if (exposeBoField) {
            informations.add(incident.getBo().getInfo());
        }

        if (exposeInfoField) {
            informations.add(incident.getInfo());
        }

        return StringUtils.join(informations, "\n\n");
    }
}
