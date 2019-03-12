package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.AlarmTextType;
import at.wrk.coceso.alarm.text.service.normalizer.NumberNormalizer;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlarmTextTargetFactoryTest {
    private AlarmTextTargetFactory sut;
    private IncidentService incidentService;
    private NumberNormalizer phoneNumberNormalizer;
    private NumberNormalizer otherNumberNormalizer;

    @Before
    public void init() {
        incidentService = mock(IncidentService.class);

        phoneNumberNormalizer = mock(NumberNormalizer.class);
        when(phoneNumberNormalizer.getSupportedUriSchema()).thenReturn("tel");

        otherNumberNormalizer = mock(NumberNormalizer.class);
        when(otherNumberNormalizer.getSupportedUriSchema()).thenReturn("other");

        // TODO: Split tests for unit target factory and alarm text target factory
        ImmutableList<NumberNormalizer> numberNormalizers = ImmutableList.of(phoneNumberNormalizer, otherNumberNormalizer);
        sut = new AlarmTextTargetFactory(incidentService, new UnitTargetFactory(numberNormalizers));
    }

    @Test
    public void getTargets_incidentHasCrewAssigned_returnPhoneNumbers() {
        int incidentId = 5;

        Set<User> crew = ImmutableSet.of(
                createMockedUser("contact1"),
                createMockedUser("tel:contact2\ncontact3"));
        Unit unit = mock(Unit.class);
        when(unit.getCrew()).thenReturn(crew);

        Incident incident = mock(Incident.class);
        when(incident.getUnits()).thenReturn(ImmutableMap.of(unit, TaskState.ZBO));

        when(incidentService.getById(incidentId)).thenReturn(incident);

        when(phoneNumberNormalizer.normalize("contact1")).thenReturn("contact1");
        when(phoneNumberNormalizer.normalize("contact2")).thenReturn("cont2");
        when(phoneNumberNormalizer.normalize("contact3")).thenReturn("");

        Map<String, List<String>> targetsMap = sut.createTargetList(incidentId, AlarmTextType.INCIDENT_INFORMATION);

        assertThat(targetsMap, hasEntry(equalTo("tel"), containsInAnyOrder("contact1", "cont2")));
    }

    @Test
    public void getTargets_incidentHasOtherNumber_returnValidTargets() {
        int incidentId = 5;

        Set<User> crew = ImmutableSet.of(
                createMockedUser("other:contact1"),
                createMockedUser("contact2\nother:contact3"));
        Unit unit = mock(Unit.class);
        when(unit.getCrew()).thenReturn(crew);

        Incident incident = mock(Incident.class);
        when(incident.getUnits()).thenReturn(ImmutableMap.of(unit, TaskState.ZBO));

        when(incidentService.getById(incidentId)).thenReturn(incident);

        when(otherNumberNormalizer.normalize("contact1")).thenReturn("contact1");
        when(phoneNumberNormalizer.normalize("contact2")).thenReturn("cont");
        when(otherNumberNormalizer.normalize("contact3")).thenReturn("");

        Map<String, List<String>> targetsMap = sut.createTargetList(incidentId, AlarmTextType.INCIDENT_INFORMATION);

        assertThat(targetsMap, hasEntry(equalTo("tel"), containsInAnyOrder("cont")));
        assertThat(targetsMap, hasEntry(equalTo("other"), containsInAnyOrder("contact1")));
    }

    @Test
    public void getTargets_incidentHasUnitWithValidAni_returnValidTargets() {
        int incidentId = 5;

        Set<User> crew = ImmutableSet.of(createMockedUser("other:contact1"));
        Unit unit = mock(Unit.class);
        when(unit.getCrew()).thenReturn(crew);
        when(unit.getAni()).thenReturn("contact2");

        Incident incident = mock(Incident.class);
        when(incident.getUnits()).thenReturn(ImmutableMap.of(unit, TaskState.ZBO));

        when(incidentService.getById(incidentId)).thenReturn(incident);

        when(otherNumberNormalizer.normalize("contact1")).thenReturn("contact1");
        when(phoneNumberNormalizer.normalize("contact2")).thenReturn("cont");

        Map<String, List<String>> targetsMap = sut.createTargetList(incidentId, AlarmTextType.INCIDENT_INFORMATION);

        assertThat(targetsMap, hasEntry(equalTo("tel"), containsInAnyOrder("cont")));
        assertThat(targetsMap, hasEntry(equalTo("other"), containsInAnyOrder("contact1")));
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

        when(phoneNumberNormalizer.normalize("contact1")).thenReturn("contact1");
        when(phoneNumberNormalizer.normalize("contact2")).thenReturn("contact2");

        Map<String, List<String>> targetsMap = sut.createTargetList(incidentId, AlarmTextType.CASUSNUMBER_BOOKING);

        assertThat(targetsMap, hasEntry(equalTo("tel"), contains("contact1")));
    }

    private User createMockedUser(final String contactString) {
        User user = mock(User.class);
        when(user.getContact()).thenReturn(contactString);
        return user;
    }
}