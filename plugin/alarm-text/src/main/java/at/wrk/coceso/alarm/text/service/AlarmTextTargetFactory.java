package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.alarm.text.configuration.AlarmTextConfiguration;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.service.IncidentService;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Transactional
public class AlarmTextTargetFactory {
    private final IncidentService incidentService;
    private final NumberNormalizer numberNormalizer;
    private final AlarmTextConfiguration configuration;

    @Autowired
    public AlarmTextTargetFactory(
            final IncidentService incidentService,
            final NumberNormalizer numberNormalizer,
            final AlarmTextConfiguration configuration) {
        this.incidentService = incidentService;
        this.numberNormalizer = numberNormalizer;
        this.configuration = configuration;
    }

    public List<String> createTargetList(final int incidentId, final AlarmTextType type) {
        List<String> targets = ImmutableList.of();
        Incident incident = incidentService.getById(incidentId);
        if (incident != null) {
            Stream<Unit> unitStream = incident.getUnits()
                    .keySet()
                    .stream();
            if (type == AlarmTextType.CASUSNUMBER_BOOKING) {
                unitStream = unitStream.filter(Unit::isTransportVehicle);
            }

            List<String> validTargets = unitStream
                    .map(this::getTargetsOfUnit)
                    .flatMap(this::streamContacts)
                    .collect(Collectors.toList());
            targets = ImmutableList.copyOf(validTargets);
        }

        return targets;
    }

    private List<String> getTargetsOfUnit(final Unit unit) {
        List<String> contacts = unit.getCrew()
                .stream()
                .map(User::getContact)
                .collect(Collectors.toList());

        if (unit.getAni() != null) {
            contacts.add(unit.getAni());
        }

        return ImmutableList.copyOf(contacts);
    }

    private Stream<String> streamContacts(final Collection<String> unvalidatedTargets) {
        return unvalidatedTargets
                .stream()
                .flatMap(AlarmTextTargetFactory::splitByLineBreak)
                .map(this::normalizeIfNecessary)
                .filter(StringUtils::isNotBlank);
    }

    private String normalizeIfNecessary(final String targetUri) {
        String normalizedString;
        if (StringUtils.startsWithAny(targetUri, configuration.getTransparentUriSchemas().toArray(new CharSequence[1]))) {
            normalizedString = targetUri;
        } else {
            normalizedString = numberNormalizer.normalize(targetUri);
        }

        return normalizedString;
    }

    private static Stream<String> splitByLineBreak(final String contactString) {
        return Stream.of(StringUtils.split(contactString, "\n"));
    }
}
