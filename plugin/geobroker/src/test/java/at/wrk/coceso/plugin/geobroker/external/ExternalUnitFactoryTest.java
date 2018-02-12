package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalUnitFactoryTest {
    private ExternalUnitFactory sut;
    private ExternalUnitIdGenerator unitIdGenerator;
    private ExternalUnitTokenGenerator tokenGenerator;
    private ExternalIncidentIdGenerator incidentIdGenerator;
    private TargetPointExtractor targetPointExtractor;

    @Before
    public void init() {
        unitIdGenerator = mock(ExternalUnitIdGenerator.class);
        tokenGenerator = mock(ExternalUnitTokenGenerator.class);
        incidentIdGenerator = mock(ExternalIncidentIdGenerator.class);
        targetPointExtractor = mock(TargetPointExtractor.class);
        sut = new ExternalUnitFactory(
                unitIdGenerator,
                tokenGenerator,
                incidentIdGenerator,
                targetPointExtractor);
    }

    @Test
    public void createExternalUnit_returnExternalUnit() {
        String externalUnitId = unitIdGeneratorReturns(5, 42);

        Unit unit = new Unit(5);
        unit.setConcern(new Concern(42));

        String externalToken = tokenGeneratorReturns(unit);

        GeoBrokerUnit externalUnit = sut.createExternalUnit(unit);

        assertThat(externalUnit.getId(), equalTo(externalUnitId));
        assertThat(externalUnit.getToken(), equalTo(externalToken));
    }

    @Test
    public void createExternalUnit_returnMappedIncidents() {
        int unitId = 5;
        int concernId = 42;
        unitIdGeneratorReturns(unitId, concernId);

        Unit unit = new Unit(unitId);
        unit.setConcern(new Concern(concernId));
        unit.setIncidentsSlim(ImmutableMap.of(3, TaskState.ZAO, 4, TaskState.ZAO));

        when(incidentIdGenerator.generateExternalIncidentId(new Incident(3))).thenReturn("extId-3");
        when(incidentIdGenerator.generateExternalIncidentId(new Incident(4))).thenReturn("extId-4");

        tokenGeneratorReturns(unit);

        GeoBrokerUnit externalUnit = sut.createExternalUnit(unit);

        assertThat(externalUnit.getIncidents(), contains("extId-3", "extId-4"));
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