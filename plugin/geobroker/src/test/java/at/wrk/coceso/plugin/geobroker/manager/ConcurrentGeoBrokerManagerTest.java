package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.plugin.geobroker.action.factory.UnitActionFactory;
import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import at.wrk.coceso.plugin.geobroker.external.TargetPointExtractor;
import at.wrk.coceso.plugin.geobroker.filter.IncidentFilter;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerIncidentPublisher;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerUnitPublisher;
import at.wrk.coceso.plugin.geobroker.utils.CachedIncidents;
import at.wrk.coceso.plugin.geobroker.utils.GeoBrokerUnits;
import at.wrk.coceso.plugin.geobroker.utils.Strings;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        sut = new ConcurrentGeoBrokerManager(
                unitPublisher,
                incidentPublisher,
                mock(TargetPointExtractor.class),
                true,
                incidentFilter,
                mock(UnitActionFactory.class),
                mock(OneTimeActionManager.class));
    }

    @Test
    public void updateUnit_publishUpdate() {
        GeoBrokerUnit updatedUnit = GeoBrokerUnits.random();

        sut.unitUpdated(new CachedUnit(updatedUnit, Map.of(), 21, UnitType.Portable, 1));

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

        verify(incidentPublisher).incidentDeleted(incident.getGeoBrokerIncidentId());
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

        sut.unitUpdated(new CachedUnit(unit, Map.of(), 21, UnitType.Portable, 1));
        reset(unitPublisher);

        sut.incidentUpdated(CachedIncidents.random(Map.of(externalUnitId, TaskState.ZBO)));

        verify(unitPublisher).unitUpdated(any());
    }

    private String randomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }
}
