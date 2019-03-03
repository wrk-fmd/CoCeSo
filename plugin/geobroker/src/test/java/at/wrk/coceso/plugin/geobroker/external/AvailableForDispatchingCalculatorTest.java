package at.wrk.coceso.plugin.geobroker.external;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitState;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class AvailableForDispatchingCalculatorTest {
    private AvailableForDispatchingCalculator sut;

    @Before
    public void init() {
        sut = new AvailableForDispatchingCalculator();
    }

    @Test
    public void unitIsEB_returnTrue() {
        Unit unit = new Unit(42);
        unit.setState(UnitState.EB);

        boolean availableForDispatching = sut.isAvailableForDispatching(unit);

        assertThat(availableForDispatching, equalTo(true));
    }

    @Test
    @Parameters({"Task", "Transport", "Standby", "Treatment"})
    public void unitHasBusyIncidentAssigned_returnFalse(final IncidentType incidentType) {
        Unit unit = new Unit(42);
        unit.setState(UnitState.EB);
        unit.addIncident(createIncident(incidentType), TaskState.ZBO);

        boolean availableForDispatching = sut.isAvailableForDispatching(unit);

        assertThat(availableForDispatching, equalTo(false));
    }

    @Test
    @Parameters({"Relocation", "ToHome", "HoldPosition"})
    public void unitHasNonBusyIncidentAssigned_returnTrue(final IncidentType incidentType) {
        Unit unit = new Unit(42);
        unit.setState(UnitState.EB);
        unit.addIncident(createIncident(incidentType), TaskState.ZBO);

        boolean availableForDispatching = sut.isAvailableForDispatching(unit);

        assertThat(availableForDispatching, equalTo(true));
    }

    private Incident createIncident(final IncidentType incidentType) {
        Incident incident = new Incident();
        incident.setType(incidentType);
        return incident;
    }
}