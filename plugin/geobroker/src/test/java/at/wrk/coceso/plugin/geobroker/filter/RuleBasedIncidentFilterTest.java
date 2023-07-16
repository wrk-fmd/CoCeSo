package at.wrk.coceso.plugin.geobroker.filter;

import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.data.CachedIncident;
import at.wrk.coceso.plugin.geobroker.utils.GeoBrokerIncidents;
import at.wrk.coceso.plugin.geobroker.utils.GeoBrokerPoints;
import com.google.common.collect.ImmutableMap;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;

@RunWith(JUnitParamsRunner.class)
public class RuleBasedIncidentFilterTest {
    private IncidentFilter sut;

    @Before
    public void init() {
        sut = new RuleBasedIncidentFilter();
    }

    @Parameters({"Task", "Transport", "Relocation"})
    @Test
    public void supportedIncidentType_returnTrue(final IncidentType incidentType) {
        CachedIncident incident = new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of("unit1", TaskState.ZBO, "unit2", TaskState.ABO),
                GeoBrokerPoints.randomPoint(),
                42,
                7,
                incidentType,
                IncidentState.InProgress
        );

        boolean incidentRelevantForGeoBroker = sut.isIncidentRelevantForGeoBroker(incident);

        MatcherAssert.assertThat(incidentRelevantForGeoBroker, equalTo(true));
    }

    @Parameters({"HoldPosition", "Standby", "ToHome", "Treatment"})
    @Test
    public void notSupportedIncidentType_returnFalse(final IncidentType incidentType) {
        CachedIncident incident = new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of("unit1", TaskState.ZBO, "unit2", TaskState.ABO),
                GeoBrokerPoints.randomPoint(),
                42,
                7,
                incidentType,
                IncidentState.InProgress
        );

        boolean incidentRelevantForGeoBroker = sut.isIncidentRelevantForGeoBroker(incident);

        MatcherAssert.assertThat(incidentRelevantForGeoBroker, equalTo(false));
    }

    @Parameters({"Open", "Demand", "InProgress"})
    @Test
    public void notDone_returnTrue(final IncidentState incidentState) {
        CachedIncident incident = new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of("unit1", TaskState.ZBO, "unit2", TaskState.ABO),
                GeoBrokerPoints.randomPoint(),
                42,
                7,
                IncidentType.Task,
                incidentState
        );

        boolean incidentRelevantForGeoBroker = sut.isIncidentRelevantForGeoBroker(incident);

        MatcherAssert.assertThat(incidentRelevantForGeoBroker, equalTo(true));
    }

    @Test
    public void done_returnFalse() {
        CachedIncident incident = new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of("unit1", TaskState.ZBO, "unit2", TaskState.ABO),
                GeoBrokerPoints.randomPoint(),
                42,
                7,
                IncidentType.Task,
                IncidentState.Done
        );

        boolean incidentRelevantForGeoBroker = sut.isIncidentRelevantForGeoBroker(incident);

        MatcherAssert.assertThat(incidentRelevantForGeoBroker, equalTo(false));
    }

    @Test
    public void emptyAssignedUnitsAndOpen_returnTrue() {
        CachedIncident incident = new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of(),
                GeoBrokerPoints.randomPoint(),
                42,
                7,
                IncidentType.Task,
                IncidentState.Open
        );

        boolean incidentRelevantForGeoBroker = sut.isIncidentRelevantForGeoBroker(incident);

        MatcherAssert.assertThat(incidentRelevantForGeoBroker, equalTo(true));
    }

    @Test
    public void emptyAssignedUnitsAndInProgress_returnFalse() {
        CachedIncident incident = new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of(),
                GeoBrokerPoints.randomPoint(),
                42,
                7,
                IncidentType.Task,
                IncidentState.InProgress
        );

        boolean incidentRelevantForGeoBroker = sut.isIncidentRelevantForGeoBroker(incident);

        MatcherAssert.assertThat(incidentRelevantForGeoBroker, equalTo(false));
    }

    @Parameters({"Detached"})
    @Test
    public void unitsNoLongerAtIncidentAndInProgress_returnFalse(final TaskState taskState) {
        CachedIncident incident = new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of("unit1", taskState),
                GeoBrokerPoints.randomPoint(),
                42,
                7,
                IncidentType.Task,
                IncidentState.InProgress
        );

        boolean incidentRelevantForGeoBroker = sut.isIncidentRelevantForGeoBroker(incident);

        MatcherAssert.assertThat(incidentRelevantForGeoBroker, equalTo(false));
    }

    @Parameters({"Assigned", "ZBO", "ABO", "ZAO", "AAO"})
    @Test
    public void unitsStillAtIncidentAndInProgress_returnTrue(final TaskState taskState) {
        CachedIncident incident = new CachedIncident(
                GeoBrokerIncidents.random(),
                ImmutableMap.of("unit1", taskState),
                GeoBrokerPoints.randomPoint(),
                42,
                7,
                IncidentType.Task,
                IncidentState.InProgress
        );

        boolean incidentRelevantForGeoBroker = sut.isIncidentRelevantForGeoBroker(incident);

        MatcherAssert.assertThat(incidentRelevantForGeoBroker, equalTo(true));
    }
}
