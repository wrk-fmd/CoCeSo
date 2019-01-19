package at.wrk.coceso.alarm.text.controller;

import at.wrk.coceso.alarm.text.api.CreateAlarmTextRequest;
import at.wrk.coceso.alarm.text.api.CreateAlarmTextResponse;
import at.wrk.coceso.alarm.text.api.SendAlarmTextRequest;
import at.wrk.coceso.alarm.text.api.SendAlarmTextResponse;
import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;
import at.wrk.coceso.alarm.text.service.AlarmTextService;
import at.wrk.coceso.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/data/alarmtext")
@PreAuthorize("@auth.hasAccessLevel('Main')")
public class AlarmTextController {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTextController.class);

    private final AlarmTextService alarmTextService;

    @Autowired
    public AlarmTextController(final AlarmTextService alarmTextService) {
        this.alarmTextService = alarmTextService;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST, produces = "application/json")
    public CreateAlarmTextResponse getAlarmTextForIncident(@RequestBody final CreateAlarmTextRequest request, final Locale locale) {
        LOG.trace("Received CreateAlarmTextRequest: {}", request);

        CreateAlarmTextResponse response;
        if (request.getIncidentId() == null || request.getType() == null) {
            response = new CreateAlarmTextResponse("Invalid parameters to create an alarm text.", null, null);
        } else {
            Optional<String> alarmText = alarmTextService.createAlarmText(request.getIncidentId(), request.getType(), locale);
            response = alarmText
                    .map(createdText -> new CreateAlarmTextResponse(null, createdText, request.getType()))
                    .orElseGet(() -> new CreateAlarmTextResponse("Failed to create alarm text.", null, null));
        }

        return response;
    }


    @RequestMapping(value = "send", method = RequestMethod.POST, produces = "application/json")
    public SendAlarmTextResponse sendAlarmText(
            @RequestBody final SendAlarmTextRequest request,
            final Locale locale,
            @AuthenticationPrincipal final User user) {
        SendAlarmTextResponse response;
        if (request.getIncidentId() == null || request.getType() == null || request.getAlarmText() == null) {
            response = new SendAlarmTextResponse("Invalid parameters to send an alarm text.");
        } else {
            SendAlarmTextResult result = alarmTextService.sendAlarmText(request.getIncidentId(), request.getAlarmText(), request.getType(), locale, user);
            if (result == SendAlarmTextResult.SUCCESS) {
                response = new SendAlarmTextResponse();
            } else if (result == SendAlarmTextResult.NO_TARGETS_FOUND) {
                response = new SendAlarmTextResponse("No targets found for the given incident id.");
            } else{
                response = new SendAlarmTextResponse("No gateway for alarm text sending configured.");
            }
        }

        return response;
    }
}
