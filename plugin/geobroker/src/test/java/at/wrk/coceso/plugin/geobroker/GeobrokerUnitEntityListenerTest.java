package at.wrk.coceso.plugin.geobroker;

import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import at.wrk.coceso.plugin.geobroker.external.ExternalUnitFactory;
import at.wrk.coceso.plugin.geobroker.external.ExternalUnitIdGenerator;
import at.wrk.coceso.plugin.geobroker.external.GeoBrokerUnitFactory;
import at.wrk.coceso.plugin.geobroker.rest.GeoBrokerUnitListener;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeobrokerUnitEntityListenerTest {
    private GeobrokerUnitEntityListener sut;
    private GeoBrokerUnitFactory unitFactory;
    private GeoBrokerUnitListener unitListener;
    private ExternalUnitIdGenerator unitIdGenerator;

    @Before
    public void init() {
        unitFactory = mock(ExternalUnitFactory.class);
        unitListener = mock(GeoBrokerUnitListener.class);
        unitIdGenerator = mock(ExternalUnitIdGenerator.class);
        sut = new GeobrokerUnitEntityListener(unitListener, unitIdGenerator, unitFactory);
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
        GeoBrokerUnit geoBrokerUnit = mock(GeoBrokerUnit.class);
        when(unitFactory.createExternalUnit(unit)).thenReturn(geoBrokerUnit);

        sut.entityChanged(unit, 0, 0, 0);

        verify(unitListener).unitUpdated(geoBrokerUnit);
    }

    @Test
    public void unitDeleted() {
        int unitId = RandomUtils.nextInt();
        String externalUnitId = RandomStringUtils.randomAlphabetic(10);
        int concernId = RandomUtils.nextInt();
        when(unitIdGenerator.generateExternalUnitId(unitId, concernId)).thenReturn(externalUnitId);

        sut.entityDeleted(unitId, concernId, 0, 0);

        verify(unitListener).unitDeleted(externalUnitId);
    }
}