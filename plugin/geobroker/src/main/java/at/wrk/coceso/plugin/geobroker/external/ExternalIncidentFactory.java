package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static at.wrk.coceso.plugin.geobroker.external.GeoBrokerPoints.mapPoint;
import static java.util.stream.Collectors.toMap;

@Component
@PropertySource(value = "classpath:geobroker.properties", ignoreResourceNotFound = true)
public class ExternalIncidentFactory implements GeoBrokerIncidentFactory {

    private final ExternalIncidentIdGenerator incidentIdGenerator;
    private final ExternalUnitIdGenerator unitIdGenerator;
    private final boolean exposeInfoField;
    private boolean exposeBoField;
    private final boolean exposeAoField;
    private final boolean exposeCasusField;

    @Autowired
    public ExternalIncidentFactory(
            final ExternalIncidentIdGenerator incidentIdGenerator,
            final ExternalUnitIdGenerator unitIdGenerator,
            @Value("${geobroker.expose.info.field:false}") final boolean exposeInfoField,
            @Value("${geobroker.expose.bo.field:false}") final boolean exposeBoField,
            @Value("${geobroker.expose.ao.field:false}") final boolean exposeAoField,
            @Value("${geobroker.expose.casus.field:false}") final boolean exposeCasusField) {
        this.incidentIdGenerator = incidentIdGenerator;
        this.unitIdGenerator = unitIdGenerator;
        this.exposeInfoField = exposeInfoField;
        this.exposeBoField = exposeBoField;
        this.exposeAoField = exposeAoField;
        this.exposeCasusField = exposeCasusField;
    }

    @Override
    public CachedIncident createExternalIncident(final Incident incident) {
        Integer concernId = incident.getConcern().getId();
        String externalIncidentId = incidentIdGenerator.generateExternalIncidentId(incident.getId(), concernId);

        Map<String, TaskState> assignedUnitIds;
        if (incident.getUnitsSlim() != null) {
            assignedUnitIds = incident.getUnitsSlim()
                    .entrySet()
                    .stream()
                    .collect(toMap(entry -> unitIdGenerator.generateExternalUnitId(entry.getKey(), concernId), Map.Entry::getValue));
        } else {
            assignedUnitIds = Map.of();
        }

        Map<String, String> externalAssignedUnitsMap = assignedUnitIds
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().name()));

        GeoBrokerIncident geoBrokerIncident = new GeoBrokerIncident(
                externalIncidentId,
                incident.getType() != null ? incident.getType().name() : "Unknown",
                incident.isPriority(),
                incident.isBlue(),
                createGeoBrokerInfo(incident),
                mapPoint(incident.getBo()),
                mapPoint(incident.getAo()),
                externalAssignedUnitsMap);
        return new CachedIncident(
                geoBrokerIncident,
                assignedUnitIds,
                mapPoint(incident.getAo()),
                concernId,
                incident.getId(),
                incident.getType(),
                incident.getState());
    }

    private String createGeoBrokerInfo(final Incident incident) {
        List<String> infoFieldLines = new ArrayList<>();

        if (exposeBoField && incident.getBo() != null && incident.getBo().getInfo() != null) {
            infoFieldLines.add("BO: " + incident.getBo().getInfo());
        }

        if (exposeAoField && incident.getAo() != null && incident.getAo().getInfo() != null) {
            infoFieldLines.add("AO: " + incident.getAo().getInfo());
        }

        if (exposeCasusField && StringUtils.isNotBlank(incident.getCasusNr())) {
            infoFieldLines.add("Casus: " + incident.getCasusNr());
        }

        if (exposeInfoField && incident.getInfo() != null) {
            infoFieldLines.add(incident.getInfo());
        }

        return StringUtils.join(infoFieldLines, "\n\n");
    }
}
