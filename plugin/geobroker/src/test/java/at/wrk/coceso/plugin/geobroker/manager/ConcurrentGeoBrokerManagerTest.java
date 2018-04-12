package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import at.wrk.coceso.plugin.geobroker.external.TargetPointExtractor;
import at.wrk.coceso.plugin.geobroker.filter.IncidentFilter;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerIncidentPublisher;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerUnitPublisher;
import at.wrk.coceso.plugin.geobroker.utils.CachedIncidents;
import at.wrk.coceso.plugin.geobroker.utils.GeoBrokerUnits;
import at.wrk.coceso.plugin.geobroker.utils.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ConcurrentGeoBrokerManagerTest {

    private GeoBrokerUnitPublisher unitPublisher;
    private GeoBrokerIncidentPublisher incidentPublisher;
    private ConcurrentGeoBrokerManager sut;
    private IncidentFilter incidentFilter;

    @Before
    public void init() {
        unitPublisher = mock(GeoBrokerUnitPublisher.class);
        incidentPublisher = mock(GeoBrokerIncidentPublisher.class);
        incidentFilter = mock(IncidentFilter.class);
        sut = new ConcurrentGeoBrokerManager(unitPublisher, incidentPublisher, mock(TargetPointExtractor.class), true, incidentFilter);
    }

    @Test
    public void updateUnit_publishUpdate() {
        GeoBrokerUnit updatedUnit = GeoBrokerUnits.random();

        sut.unitUpdated(new CachedUnit(updatedUnit, ImmutableMap.of(), UnitType.Portable, 1));

        verify(unitPublisher).unitUpdated(any(GeoBrokerUnit.class));
    }

    @Test
    public void deleteUnit_publishDelete() {
        String externalUnitId = randomString();

        sut.unitDeleted(externalUnitId);

        verify(unitPublisher).unitDeleted(externalUnitId);
    }

    @Test
    public void updateIncident_filterReturnsTrue_publishUpdate() {
        CachedIncident incident = CachedIncidents.random();
        when(incidentFilter.isIncidentRelevantForGeoBroker(incident)).thenReturn(true);

        sut.incidentUpdated(incident);

        verify(incidentPublisher).incidentUpdated(incident.getIncident());
    }

    @Test
    public void updateIncident_filterReturnsFalse_removeIncident() {
        CachedIncident incident = CachedIncidents.random();
        when(incidentFilter.isIncidentRelevantForGeoBroker(incident)).thenReturn(false);

        sut.incidentUpdated(incident);

        verify(incidentPublisher).incidentDeleted(incident.getId());
    }

    @Test
    public void deleteIncident_publishDelete() {
        String externalIncidentId = Strings.randomString();

        sut.incidentDeleted(externalIncidentId);

        verify(incidentPublisher).incidentDeleted(externalIncidentId);
    }

    @Test
    public void updateIncident_publishUnitUpdate() {
        String externalUnitId = randomString();
        GeoBrokerUnit unit = GeoBrokerUnits.random(externalUnitId);

        sut.unitUpdated(new CachedUnit(unit, ImmutableMap.of(), UnitType.Portable, 1));
        reset(unitPublisher);

        sut.incidentUpdated(CachedIncidents.random(ImmutableMap.of(externalUnitId, TaskState.ZBO)));

        verify(unitPublisher).unitUpdated(any());
    }

    private String randomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }
}