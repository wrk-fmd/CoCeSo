package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.utils.GeoBrokerIncidents;
import com.google.common.collect.ImmutableMap;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static at.wrk.coceso.plugin.geobroker.utils.GeoBrokerPoints.randomPoint;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class TargetPointExtractorTest {
    private TargetPointExtractor sut;

    @Before
    public void init() {
        sut = new TargetPointExtractor();
    }

    @Test
    @Parameters({"Assigned", "ZBO"})
    public void task_assignedOrZbo_returnBO(final TaskState taskState) {
        CachedIncident cachedIncident = createIncidentOfType(IncidentType.Task);

        GeoBrokerPoint targetPoint = sut.getTargetPoint(cachedIncident, taskState);

        assertThat(targetPoint, equalTo(cachedIncident.getIncident().getLocation()));
    }

    @Test
    public void task_zao_returnAO() {
        CachedIncident cachedIncident = createIncidentOfType(IncidentType.Task);

        GeoBrokerPoint targetPoint = sut.getTargetPoint(cachedIncident, TaskState.ZAO);

        assertThat(targetPoint, equalTo(cachedIncident.getDestination()));
    }

    @Test
    @Parameters({"ABO", "AAO"})
    public void task_notMoving_returnNoPoint(final TaskState taskState) {
        CachedIncident cachedIncident = createIncidentOfType(IncidentType.Task);

        GeoBrokerPoint targetPoint = sut.getTargetPoint(cachedIncident, taskState);

        assertThat(targetPoint, nullValue());
    }

    @Test
    @Parameters({"Assigned", "ZBO", "ZAO"})
    public void toHome_assignedOrZbo_returnBO(final TaskState taskState) {
        CachedIncident cachedIncident = createIncidentOfType(IncidentType.ToHome);

        GeoBrokerPoint targetPoint = sut.getTargetPoint(cachedIncident, taskState);

        assertThat(targetPoint, equalTo(cachedIncident.getDestination()));
    }

    @Test
    @Parameters({"ABO", "AAO"})
    public void toHome_notMoving_returnNoPoint(final TaskState taskState) {
        CachedIncident cachedIncident = createIncidentOfType(IncidentType.ToHome);

        GeoBrokerPoint targetPoint = sut.getTargetPoint(cachedIncident, taskState);

        assertThat(targetPoint, nullValue());
    }

    @Test
    @Parameters({"Assigned", "ZBO", "ZAO"})
    public void relocation_assignedOrZbo_returnBO(final TaskState taskState) {
        CachedIncident cachedIncident = createIncidentOfType(IncidentType.Relocation);

        GeoBrokerPoint targetPoint = sut.getTargetPoint(cachedIncident, taskState);

        assertThat(targetPoint, equalTo(cachedIncident.getDestination()));
    }

    @Test
    @Parameters({"ABO", "AAO"})
    public void relocation_notMoving_returnNoPoint(final TaskState taskState) {
        CachedIncident cachedIncident = createIncidentOfType(IncidentType.Relocation);

        GeoBrokerPoint targetPoint = sut.getTargetPoint(cachedIncident, taskState);

        assertThat(targetPoint, nullValue());
    }

    private CachedIncident createIncidentOfType(final IncidentType incidentType) {
        return new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of(),
                randomPoint(),
                1,
                incidentType,
                IncidentState.InProgress);
    }
}