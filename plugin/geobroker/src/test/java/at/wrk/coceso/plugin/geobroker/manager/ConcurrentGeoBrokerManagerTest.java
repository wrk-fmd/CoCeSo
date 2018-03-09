package at.wrk.coceso.plugin.geobroker.manager;

import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import at.wrk.coceso.plugin.geobroker.external.TargetPointExtractor;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class ConcurrentGeoBrokerManagerTest {

    private GeoBrokerUnitPublisher unitPublisher;
    private GeoBrokerIncidentPublisher incidentPublisher;
    private ConcurrentGeoBrokerManager sut;

    @Before
    public void init() {
        unitPublisher = mock(GeoBrokerUnitPublisher.class);
        incidentPublisher = mock(GeoBrokerIncidentPublisher.class);
        sut = new ConcurrentGeoBrokerManager(unitPublisher, incidentPublisher, mock(TargetPointExtractor.class), true);
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
    public void updateIncident_publishUpdate() {
        CachedIncident incident = CachedIncidents.random();

        sut.incidentUpdated(incident);

        verify(incidentPublisher).incidentUpdated(incident.getIncident());
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

        sut.incidentUpdated(CachedIncidents.random(ImmutableList.of(externalUnitId)));

        verify(unitPublisher).unitUpdated(any());
    }

    private String randomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }
}