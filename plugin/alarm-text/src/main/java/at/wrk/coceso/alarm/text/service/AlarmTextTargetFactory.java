package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.IncidentService;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Transactional
public class AlarmTextTargetFactory {

    private final IncidentService incidentService;
    private final UnitTargetFactory unitTargetFactory;

    @Autowired
    public AlarmTextTargetFactory(
            final IncidentService incidentService,
            final UnitTargetFactory unitTargetFactory) {
        this.incidentService = incidentService;
        this.unitTargetFactory = unitTargetFactory;
    }

    public Map<String, List<String>> createTargetList(final int incidentId, final AlarmTextType type) {
        ImmutableMap<String, List<String>> targets = ImmutableMap.of();
        Incident incident = incidentService.getById(incidentId);
        if (incident != null) {
            Stream<Unit> unitStream = incident.getUnits()
                    .keySet()
                    .stream();
            if (type == AlarmTextType.CASUSNUMBER_BOOKING) {
                unitStream = unitStream.filter(Unit::isTransportVehicle);
            }

            Map<String, List<String>> validTargets = unitTargetFactory.getValidTargetsOfUnitStream(unitStream);
            targets = ImmutableMap.copyOf(validTargets);
        }

        return targets;
    }
}
