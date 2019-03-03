package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalUnitFactoryTest {
    private ExternalUnitFactory sut;
    private ExternalUnitIdGenerator unitIdGenerator;
    private ExternalUnitTokenGenerator tokenGenerator;
    private ExternalIncidentIdGenerator incidentIdGenerator;
    private AvailableForDispatchingCalculator availableForDispatchingCalculator;

    @Before
    public void init() {
        unitIdGenerator = mock(ExternalUnitIdGenerator.class);
        tokenGenerator = mock(ExternalUnitTokenGenerator.class);
        incidentIdGenerator = mock(ExternalIncidentIdGenerator.class);
        availableForDispatchingCalculator = mock(AvailableForDispatchingCalculator.class);
        sut = new ExternalUnitFactory(
                unitIdGenerator,
                tokenGenerator,
                incidentIdGenerator,
                availableForDispatchingCalculator);
    }

    @Test
    public void createExternalUnit_returnExternalUnit() {
        String externalUnitId = unitIdGeneratorReturns(5, 42);

        Unit unit = new Unit(5);
        unit.setConcern(new Concern(42));
        boolean isAvailableForDispatching = RandomUtils.nextBoolean();
        when(availableForDispatchingCalculator.isAvailableForDispatching(unit)).thenReturn(isAvailableForDispatching);

        String externalToken = tokenGeneratorReturns(unit);

        CachedUnit externalUnit = sut.createExternalUnit(unit);

        assertThat(externalUnit.getUnitType(), equalTo(unit.getType()));
        assertThat(externalUnit.getUnit().getId(), equalTo(externalUnitId));
        assertThat(externalUnit.getUnit().getToken(), equalTo(externalToken));
        assertThat(externalUnit.getUnit().getAvailableForDispatching(), equalTo(isAvailableForDispatching));
    }

    @Test
    public void createExternalUnit_returnMappedIncidents() {
        int unitId = 5;
        int concernId = 42;
        unitIdGeneratorReturns(unitId, concernId);

        Unit unit = new Unit(unitId);
        unit.setConcern(new Concern(concernId));
        unit.setIncidentsSlim(ImmutableMap.of(3, TaskState.ZAO, 4, TaskState.ZBO));

        when(incidentIdGenerator.generateExternalIncidentId(3, concernId)).thenReturn("extId-3");
        when(incidentIdGenerator.generateExternalIncidentId(4, concernId)).thenReturn("extId-4");

        tokenGeneratorReturns(unit);

        CachedUnit externalUnit = sut.createExternalUnit(unit);

        assertThat(externalUnit.getIncidentsWithState(), allOf(
                hasEntry("extId-3", TaskState.ZAO),
                hasEntry("extId-4", TaskState.ZBO)
        ));
    }

    private String unitIdGeneratorReturns(final int unitId, final int concernId) {
        String externalUnitId = "extUnitId";
        when(unitIdGenerator.generateExternalUnitId(unitId, concernId)).thenReturn(externalUnitId);
        return externalUnitId;
    }

    private String tokenGeneratorReturns(final Unit unit) {
        String externalToken = "externalToken";
        when(tokenGenerator.generateToken(unit)).thenReturn(externalToken);
        return externalToken;
    }
}