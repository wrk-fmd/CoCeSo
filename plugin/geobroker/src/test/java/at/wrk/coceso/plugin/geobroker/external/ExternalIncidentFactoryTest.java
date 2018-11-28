package at.wrk.coceso.plugin.geobroker.external;


import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.geocode.LatLng;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalIncidentFactoryTest {

    private ExternalIncidentFactory sut;
    private ExternalIncidentIdGenerator incidentIdGenerator;
    private ExternalUnitIdGenerator unitIdGenerator;

    @Before
    public void init() {
        incidentIdGenerator = mock(ExternalIncidentIdGenerator.class);
        unitIdGenerator = mock(ExternalUnitIdGenerator.class);
        sut = new ExternalIncidentFactory(incidentIdGenerator, unitIdGenerator, true, true);
    }

    @Test
    public void incidentWithAllInformation_returnExternalIncident() {
        int concernId = 42;
        int incidentId = 5;
        Incident incident = createIncident(concernId, incidentId, createBoPointWithInformation());

        String externalId = incidentIdGeneratorReturns(concernId, incidentId);
        String externalUnitId1 = unitIdGeneratorReturns(concernId, 1);
        String externalUnitId2 = unitIdGeneratorReturns(concernId, 2);

        CachedIncident externalIncident = sut.createExternalIncident(incident);

        assertThat(externalIncident.getConcernId(), equalTo(concernId));
        assertThat(externalIncident.getId(), equalTo(externalId));
        assertThat(externalIncident.getIncidentType(), equalTo(incident.getType()));
        assertThat(externalIncident.getIncidentState(), equalTo(incident.getState()));
        assertThat(externalIncident.getAssignedExternalUnitIds(), hasEntry(externalUnitId1, TaskState.ABO));
        assertThat(externalIncident.getAssignedExternalUnitIds(), hasEntry(externalUnitId2, TaskState.ZAO));
        assertThat(externalIncident.getIncident().getInfo(), containsString(incident.getBo().getInfo()));
    }

    @Test
    public void incidentWithoutInformationFieldsSet_returnExternalIncident() {
        int concernId = 42;
        int incidentId = 5;
        Incident incident = createIncident(concernId, incidentId, null);

        incidentIdGeneratorReturns(concernId, incidentId);
        unitIdGeneratorReturns(concernId, 1);
        unitIdGeneratorReturns(concernId, 2);

        CachedIncident externalIncident = sut.createExternalIncident(incident);

        assertThat(externalIncident.getIncident().getInfo(), equalTo(""));
    }

    private Point createBoPointWithInformation() {
        return new Point() {
            @Override
            public String getInfo() {
                return "BO Information";
            }

            @Override
            public LatLng getCoordinates() {
                return new LatLng(1.5, 2.3);
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public Point deepCopy() {
                return this;
            }
        };
    }

    private String unitIdGeneratorReturns(final int concernId, final int unitId) {
        String externalUnitId = "externalId_" + RandomStringUtils.randomAlphabetic(8);
        when(unitIdGenerator.generateExternalUnitId(unitId, concernId)).thenReturn(externalUnitId);
        return externalUnitId;
    }

    private String incidentIdGeneratorReturns(final int concernId, final int incidentId) {
        String externalId = "extId_1234";
        when(incidentIdGenerator.generateExternalIncidentId(incidentId, concernId)).thenReturn(externalId);
        return externalId;
    }

    private Incident createIncident(final int concernId, final int incidentId, final Point bo) {
        Incident incident = new Incident(incidentId);
        incident.setConcern(new Concern(concernId));
        incident.setUnitsSlim(ImmutableMap.of(
                1, TaskState.ABO,
                2, TaskState.ZAO
        ));
        incident.setType(IncidentType.Task);
        incident.setState(IncidentState.InProgress);

        incident.setBo(bo);
        return incident;
    }
}