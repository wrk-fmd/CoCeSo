package at.wrk.cocecl.dto.main;

import at.wrk.cocecl.dto.Answer;
import at.wrk.cocecl.dto.Incident;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Answer holding all active incidents of a logged in unit.
 */
public class GetActiveIncidentsAnswer extends Answer implements Serializable {
    private static final long serialVersionUID = 1L;

    private Collection<Incident> activeIncidents;

    private GetActiveIncidentsAnswer() {
        super();
    }

    private GetActiveIncidentsAnswer(final boolean success, final Collection<Incident> activeIncidents) {
        super();
        setSuccess(success);
        // NullPointerException on given null parameter intended
        this.activeIncidents = new ArrayList<>(activeIncidents);
    }

    public Collection<Incident> getActiveIncidents() {
        return activeIncidents;
    }

    public void setActiveIncidents(Collection<Incident> activeIncidents) {
        this.activeIncidents = activeIncidents;
    }

    public static GetActiveIncidentsAnswer create(boolean success, Collection<Incident> activeIncidents) {
        return new GetActiveIncidentsAnswer(success, activeIncidents);
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
                "success=" + getSuccess() + "," +
                "activeIncidents=" + activeIncidents +
                "]";
    }
}
