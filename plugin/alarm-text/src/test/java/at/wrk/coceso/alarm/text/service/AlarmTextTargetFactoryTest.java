package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.alarm.text.configuration.AlarmTextConfiguration;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlarmTextTargetFactoryTest {
    private AlarmTextTargetFactory sut;
    private IncidentService incidentService;
    private NumberNormalizer numberNormalizer;

    @Before
    public void init() {
        incidentService = mock(IncidentService.class);
        numberNormalizer = mock(NumberNormalizer.class);
        AlarmTextConfiguration alarmTextConfiguration = mock(AlarmTextConfiguration.class);
        when(alarmTextConfiguration.getTransparentUriSchemas()).thenReturn(ImmutableSet.of("t1", "t2"));
        sut = new AlarmTextTargetFactory(incidentService, numberNormalizer, alarmTextConfiguration);
    }

    @Test
    public void getTargets_incidentHasCrewAssigned_returnValidTargets() {
        int incidentId = 5;

        Set<User> crew = ImmutableSet.of(
                createMockedUser("contact1"),
                createMockedUser("contact2\ncontact3"));
        Unit unit = mock(Unit.class);
        when(unit.getCrew()).thenReturn(crew);

        Incident incident = mock(Incident.class);
        when(incident.getUnits()).thenReturn(ImmutableMap.of(unit, TaskState.ZBO));

        when(incidentService.getById(incidentId)).thenReturn(incident);

        when(numberNormalizer.normalize("contact1")).thenReturn("contact1");
        when(numberNormalizer.normalize("contact2")).thenReturn("cont");
        when(numberNormalizer.normalize("contact3")).thenReturn("");

        List<String> targetList = sut.createTargetList(incidentId, AlarmTextType.INCIDENT_INFORMATION);

        assertThat(targetList, containsInAnyOrder("contact1", "cont"));
    }

    @Test
    public void getTargets_incidentHasTransparentNumber_returnValidTargets() {
        int incidentId = 5;

        Set<User> crew = ImmutableSet.of(
                createMockedUser("t1:contact1"),
                createMockedUser("contact2\nt2:contact3"));
        Unit unit = mock(Unit.class);
        when(unit.getCrew()).thenReturn(crew);

        Incident incident = mock(Incident.class);
        when(incident.getUnits()).thenReturn(ImmutableMap.of(unit, TaskState.ZBO));

        when(incidentService.getById(incidentId)).thenReturn(incident);

        when(numberNormalizer.normalize("contact1")).thenReturn("contact1");
        when(numberNormalizer.normalize("contact2")).thenReturn("cont");
        when(numberNormalizer.normalize("contact3")).thenReturn("");

        List<String> targetList = sut.createTargetList(incidentId, AlarmTextType.INCIDENT_INFORMATION);

        assertThat(targetList, containsInAnyOrder("t1:contact1", "cont", "t2:contact3"));
    }

    @Test
    public void getTargets_incidentHasUnitWithValidAni_returnValidTargets() {
        int incidentId = 5;

        Set<User> crew = ImmutableSet.of(
                createMockedUser("t1:contact1"));
        Unit unit = mock(Unit.class);
        when(unit.getCrew()).thenReturn(crew);
        when(unit.getAni()).thenReturn("contact2");

        Incident incident = mock(Incident.class);
        when(incident.getUnits()).thenReturn(ImmutableMap.of(unit, TaskState.ZBO));

        when(incidentService.getById(incidentId)).thenReturn(incident);

        when(numberNormalizer.normalize("contact1")).thenReturn("contact1");
        when(numberNormalizer.normalize("contact2")).thenReturn("cont");

        List<String> targetList = sut.createTargetList(incidentId, AlarmTextType.INCIDENT_INFORMATION);

        assertThat(targetList, containsInAnyOrder("t1:contact1", "cont"));
    }

    @Test
    public void getTargets_casusBooking_returnValidTransportTargets() {
        int incidentId = 5;

        Set<User> crew1 = ImmutableSet.of(createMockedUser("contact1"));
        Unit unit1 = mock(Unit.class);
        when(unit1.getCrew()).thenReturn(crew1);
        when(unit1.isTransportVehicle()).thenReturn(true);

        Set<User> crew2 = ImmutableSet.of(createMockedUser("contact2"));
        Unit unit2 = mock(Unit.class);
        when(unit2.getCrew()).thenReturn(crew2);
        when(unit2.isTransportVehicle()).thenReturn(false);

        Incident incident = mock(Incident.class);
        when(incident.getUnits()).thenReturn(ImmutableMap.of(unit1, TaskState.ZBO, unit2, TaskState.ZBO));

        when(incidentService.getById(incidentId)).thenReturn(incident);

        when(numberNormalizer.normalize("contact1")).thenReturn("contact1");
        when(numberNormalizer.normalize("contact2")).thenReturn("contact2");

        List<String> targetList = sut.createTargetList(incidentId, AlarmTextType.CASUSNUMBER_BOOKING);

        assertThat(targetList, containsInAnyOrder("contact1"));
    }

    private User createMockedUser(final String contactString) {
        User user = mock(User.class);
        when(user.getContact()).thenReturn(contactString);
        return user;
    }
}