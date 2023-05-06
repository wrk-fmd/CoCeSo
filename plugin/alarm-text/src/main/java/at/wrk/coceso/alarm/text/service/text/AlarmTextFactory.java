package at.wrk.coceso.alarm.text.service.text;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.service.IncidentService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AlarmTextFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTextFactory.class);

    private static final String INCIDENT_INFORMATION_TEMPLATE_FILE = "/incidentInformationTemplate.txt";
    private static final String INCIDENT_INFORMATION_DEFAULT_TEMPLATE = "{time}: #{incidentId} {type}\n{bo}\n{info}\n{units}";

    private static final String RELOCATION_INFORMATION_TEMPLATE_FILE = "/relocationInformationTemplate.txt";
    private static final String RELOCATION_INFORMATION_DEFAULT_TEMPLATE = "{time}: #{incidentId} {type}\n{ao}\n{info}";

    private static final String CASUSNUMBER_TEMPLATE_FILE = "/casusNumberBookingTemplate.txt";
    private static final String CASUSNUMBER_DEFAULT_TEMPLATE = "{time}: #{incidentId} {type}\n{bo}\n{info}\n{units}";

    private final IncidentService incidentService;
    private final MessageSource messageSource;

    @Autowired
    public AlarmTextFactory(
            final IncidentService incidentService,
            final MessageSource messageSource) {
        this.incidentService = incidentService;
        this.messageSource = messageSource;
    }

    @Transactional
    public Optional<String> createAlarmText(final int incidentId, final AlarmTextType type, final Locale locale) {
        Optional<String> alarmText = Optional.empty();

        Incident incident = incidentService.getById(incidentId);
        if (incident != null) {
            alarmText = Optional.of(buildAlarmText(incident, type, locale));
        } else {
            LOG.info("Incident #{} does not exist. Alarm text cannot be created.");
        }

        return alarmText;
    }

    private String loadTemplate(final String templateFile, final String defaultTemplate) {
        String template;
        try {
            template = IOUtils.resourceToString(templateFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.warn("Failed to read template for alarm text creation. Default template is used. Exception message: {}", e.getMessage());
            LOG.debug("Underlying exception", e);
            template = defaultTemplate;
        }
        return template;
    }

    private String buildAlarmText(final Incident incident, final AlarmTextType alarmTextType, final Locale locale) {
        String template;
        if (alarmTextType == AlarmTextType.INCIDENT_INFORMATION) {
            if (incident.getType() == IncidentType.Relocation) {
                template = loadTemplate(RELOCATION_INFORMATION_TEMPLATE_FILE, RELOCATION_INFORMATION_DEFAULT_TEMPLATE);
            } else {
                template = loadTemplate(INCIDENT_INFORMATION_TEMPLATE_FILE, INCIDENT_INFORMATION_DEFAULT_TEMPLATE);
            }
        } else {
            template = loadTemplate(CASUSNUMBER_TEMPLATE_FILE, CASUSNUMBER_DEFAULT_TEMPLATE);
        }


        String alarmText = template;

        alarmText = alarmText.replace("{incidentId}", incident.getId() + "");
        alarmText = alarmText.replace("{time}", buildTime());
        alarmText = alarmText.replace("{type}", buildTypeString(locale, incident.getType(), incident.isBlue()));
        alarmText = alarmText.replace("{bo}", buildAddressString(incident.getBo()));
        alarmText = alarmText.replace("{ao}", buildAddressString(incident.getAo()));
        alarmText = alarmText.replace("{boCoordinateUrl}", buildCoordinateUrl(incident.getBo()));
        alarmText = alarmText.replace("{aoCoordinateUrl}", buildCoordinateUrl(incident.getAo()));
        alarmText = alarmText.replace("{info}", limitNullableStringToCharacters(incident.getInfo(), 80));
        alarmText = alarmText.replace("{unitsName}", getLocalizedMessage("units", locale));
        alarmText = alarmText.replace("{units}", buildUnitsString(incident.getUnits().keySet()));
        alarmText = alarmText.replace("{casusnumber}", limitNullableStringToCharacters(incident.getCasusNr(), 40));
        alarmText = alarmText.replace("{emergencyRoomType}", buildEmergencyRoomType(incident.getPatient()));

        return alarmText;
    }

    private String buildEmergencyRoomType(final Patient patient) {
        String emergencyRoomType = "";
        if (patient != null) {
            emergencyRoomType = limitNullableStringToCharacters(patient.getErtype(), 20);
        }

        return emergencyRoomType;
    }

    private String buildUnitsString(final Set<Unit> units) {
        String unitsString = "";
        if (units != null && !units.isEmpty()) {
            Set<String> unitCallSigns = units.stream().map(Unit::getCall).collect(Collectors.toSet());
            unitsString = StringUtils.join(unitCallSigns, ", ");
            unitsString = unitsString.length() > 50 ? unitsString.substring(0, 48) + "..." : unitsString;
        }

        return unitsString;
    }

    private String limitNullableStringToCharacters(final String info, final int maximumCharacters) {
        String infoString = "";
        if (info != null) {
            infoString = info.length() > maximumCharacters ? info.substring(0, maximumCharacters - 2) + "..." : info;
        }

        return infoString;
    }

    private String buildCoordinateUrl(final Point point) {
        String pointUrl = "";

        if (point != null && point.getCoordinates() != null) {
            // US locale is needed for dot in floating point number.
            pointUrl = String.format(
                    Locale.US,
                    "https://www.google.com/maps/search/?api=1&query=%f%%2C%f",
                    point.getCoordinates().getLat(),
                    point.getCoordinates().getLng());
        } else {
            LOG.debug("Cannot set address url for alarm text. Address coordinate property is null.");
        }

        return pointUrl;
    }

    private String buildAddressString(final Point addressPoint) {
        String addressString = "";

        if (addressPoint != null) {
            addressString = addressPoint.toString();
        } else {
            LOG.debug("Cannot set address point for alarm text. Address point is null.");
        }

        return addressString;
    }

    private String buildTypeString(final Locale locale, final IncidentType type, final boolean blue) {
        String typeString = "";
        if (type != null) {
            typeString = getLocalizedMessage(blue ? "incident.type.task.blue" : "incident.type.task", locale);

            if (type == IncidentType.Relocation) {
                typeString += ": " + getLocalizedMessage("incident.type.relocation", locale);
            }

            if (type == IncidentType.Transport) {
                typeString += ": " + getLocalizedMessage("incident.type.transport", locale);
            }
        } else {
            LOG.debug("Cannot set type for alarm text. Type is null.");
        }

        return typeString;
    }

    private String buildTime() {
        LocalTime localTime = LocalTime.now();
        return String.format("%02d:%02d", localTime.getHour(), localTime.getMinute());
    }

    private String getLocalizedMessage(final String code, final Locale locale) {
        return messageSource.getMessage(code, null, "", locale);
    }
}
