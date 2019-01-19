package at.wrk.coceso.alarm.text.controller;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.alarm.text.api.CreateAlarmTextRequest;
import at.wrk.coceso.alarm.text.api.CreateAlarmTextResponse;
import at.wrk.coceso.alarm.text.api.SendAlarmTextRequest;
import at.wrk.coceso.alarm.text.api.SendAlarmTextResponse;
import at.wrk.coceso.alarm.text.data.SendAlarmTextResult;
import at.wrk.coceso.alarm.text.service.AlarmTextService;
import at.wrk.coceso.entity.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlarmTextControllerTest {
    private AlarmTextController sut;
    private AlarmTextService alarmTextService;
    private Locale locale;
    private User user;

    @Before
    public void init() {
        alarmTextService = mock(AlarmTextService.class);
        locale = Locale.CANADA_FRENCH;
        user = mock(User.class);
        sut = new AlarmTextController(alarmTextService);
    }

    @Test
    public void getAlarmTextForIncident_noIncidentId_returnError() {
        CreateAlarmTextRequest request = new CreateAlarmTextRequest(null, AlarmTextType.CASUSNUMBER_BOOKING);

        CreateAlarmTextResponse response = sut.getAlarmTextForIncident(request, locale);

        assertThat(response.isSuccess(), equalTo(false));
    }

    @Test
    public void getAlarmTextForIncident_noAlarmTextType_returnError() {
        CreateAlarmTextRequest request = new CreateAlarmTextRequest(5, null);

        CreateAlarmTextResponse response = sut.getAlarmTextForIncident(request, locale);

        assertThat(response.isSuccess(), equalTo(false));
    }

    @Test
    public void getAlarmTextForIncident_validRequest_alarmTextOfServiceIsReturned() {
        CreateAlarmTextRequest request = new CreateAlarmTextRequest(5, AlarmTextType.INCIDENT_INFORMATION);
        String alarmText = "This is a great alarm text.";
        when(alarmTextService.createAlarmText(request.getIncidentId(), request.getType(), locale)).thenReturn(Optional.of(alarmText));

        CreateAlarmTextResponse response = sut.getAlarmTextForIncident(request, locale);

        assertThat(response.isSuccess(), equalTo(true));
        assertThat(response.getAlarmText(), equalTo(alarmText));
        assertThat(response.getType(), equalTo(request.getType()));
    }

    @Test
    public void getAlarmTextForIncident_incidentIdUnknown_errorReturned() {
        CreateAlarmTextRequest request = new CreateAlarmTextRequest(5, AlarmTextType.INCIDENT_INFORMATION);
        when(alarmTextService.createAlarmText(request.getIncidentId(), request.getType(), locale)).thenReturn(Optional.empty());

        CreateAlarmTextResponse response = sut.getAlarmTextForIncident(request, locale);

        assertThat(response.isSuccess(), equalTo(false));
    }

    @Test
    public void sendAlarmText_noIncidentId_returnError() {
        SendAlarmTextRequest request = new SendAlarmTextRequest(null, "Alarm Text", AlarmTextType.CASUSNUMBER_BOOKING);

        SendAlarmTextResponse response = sut.sendAlarmText(request, locale, user);

        assertThat(response.isSuccess(), equalTo(false));
    }

    @Test
    public void sendAlarmText_noAlarmText_returnError() {
        SendAlarmTextRequest request = new SendAlarmTextRequest(5, null, AlarmTextType.CASUSNUMBER_BOOKING);

        SendAlarmTextResponse response = sut.sendAlarmText(request, locale, user);

        assertThat(response.isSuccess(), equalTo(false));
    }

    @Test
    public void sendAlarmText_noAlarmType_returnError() {
        SendAlarmTextRequest request = new SendAlarmTextRequest(42, "Alarm Text", null);

        SendAlarmTextResponse response = sut.sendAlarmText(request, locale, user);

        assertThat(response.isSuccess(), equalTo(false));
    }

    @Test
    public void sendAlarmText_serviceReturnsNoGatewayErrorResult_returnError() {
        SendAlarmTextRequest request = new SendAlarmTextRequest(42, "Alarm Text", AlarmTextType.INCIDENT_INFORMATION);
        when(alarmTextService.sendAlarmText(request.getIncidentId(), request.getAlarmText(), request.getType(), locale, user))
                .thenReturn(SendAlarmTextResult.NO_GATEWAY_CONFIGURED);

        SendAlarmTextResponse response = sut.sendAlarmText(request, locale, user);

        assertThat(response.isSuccess(), equalTo(false));
    }

    @Test
    public void sendAlarmText_serviceReturnsNoTargetsFoundErrorResult_returnError() {
        SendAlarmTextRequest request = new SendAlarmTextRequest(42, "Alarm Text", AlarmTextType.INCIDENT_INFORMATION);
        when(alarmTextService.sendAlarmText(request.getIncidentId(), request.getAlarmText(), request.getType(), locale, user))
                .thenReturn(SendAlarmTextResult.NO_TARGETS_FOUND);

        SendAlarmTextResponse response = sut.sendAlarmText(request, locale, user);

        assertThat(response.isSuccess(), equalTo(false));
    }

    @Test
    public void sendAlarmText_serviceReturnsSuccessResult_returnSuccess() {
        SendAlarmTextRequest request = new SendAlarmTextRequest(42, "Alarm Text", AlarmTextType.INCIDENT_INFORMATION);
        when(alarmTextService.sendAlarmText(request.getIncidentId(), request.getAlarmText(), request.getType(), locale, user))
                .thenReturn(SendAlarmTextResult.SUCCESS);

        SendAlarmTextResponse response = sut.sendAlarmText(request, locale, user);

        assertThat(response.isSuccess(), equalTo(true));
    }
}