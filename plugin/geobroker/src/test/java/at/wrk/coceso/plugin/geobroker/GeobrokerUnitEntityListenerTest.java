package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import at.wrk.coceso.plugin.geobroker.external.ExternalUnitFactory;
import at.wrk.coceso.plugin.geobroker.external.ExternalUnitIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerUnitFactory;
import at.wrk.coceso.plugin.geobroker.loader.UnitLoader;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeobrokerUnitEntityListenerTest {
    private GeobrokerUnitEntityListener sut;
    private GeoBrokerUnitFactory unitFactory;
    private GeoBrokerManager brokerManager;
    private ExternalUnitIdGenerator unitIdGenerator;
    private UnitLoader unitLoader;

    @Before
    public void init() {
        unitFactory = mock(ExternalUnitFactory.class);
        brokerManager = mock(GeoBrokerManager.class);
        unitIdGenerator = mock(ExternalUnitIdGenerator.class);
        unitLoader = mock(UnitLoader.class);
        sut = new GeobrokerUnitEntityListener(brokerManager, unitIdGenerator, unitFactory, unitLoader);
    }

    @Test
    public void serializable_notSupported() {
        boolean supported = sut.isSupported(Serializable.class);

        assertThat(supported, equalTo(false));
    }

    @Test
    public void unit_supported() {
        boolean supported = sut.isSupported(Unit.class);

        assertThat(supported, equalTo(true));
    }

    @Test
    public void unitUpdated() {
        Unit unit = mock(Unit.class);
        CachedUnit geoBrokerUnit = mock(CachedUnit.class);
        when(unitFactory.createExternalUnit(unit)).thenReturn(geoBrokerUnit);

        sut.entityChanged(unit, 0, 0, 0);

        verify(brokerManager).unitUpdated(geoBrokerUnit);
    }

    @Test
    public void contextRefreshed() {
        Unit unit = mock(Unit.class);
        CachedUnit geoBrokerUnit = mock(CachedUnit.class);
        when(unitFactory.createExternalUnit(unit)).thenReturn(geoBrokerUnit);

        when(unitLoader.loadAllUnitsOfActiveConcerns()).thenReturn(Set.of(unit));

        sut.onContextRefreshed(null);

        verify(brokerManager).unitUpdated(geoBrokerUnit);
    }

    @Test
    public void unitDeleted() {
        int unitId = RandomUtils.nextInt();
        String externalUnitId = RandomStringUtils.randomAlphabetic(10);
        int concernId = RandomUtils.nextInt();
        when(unitIdGenerator.generateExternalUnitId(unitId, concernId)).thenReturn(externalUnitId);

        sut.entityDeleted(unitId, concernId, 0, 0);

        verify(brokerManager).unitDeleted(externalUnitId);
    }
}
