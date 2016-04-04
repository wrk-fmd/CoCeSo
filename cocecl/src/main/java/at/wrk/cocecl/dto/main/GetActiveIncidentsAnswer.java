package at.wrk.cocecl.dto.main;

import at.wrk.cocecl.dto.Answer;
import at.wrk.cocecl.dto.Incident;

import java.io.Serializable;
import java.util.Collection;

/**
 * Answer holding all active incidents of a logged in unit.
 */
public class GetActiveIncidentsAnswer extends Answer implements Serializable {
    private static final long serialVersionUID = 1L;

    private Boolean success;
    private Collection<Incident> activeIncidents;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Collection<Incident> getActiveIncidents() {
        return activeIncidents;
    }

    public void setActiveIncidents(Collection<Incident> activeIncidents) {
        this.activeIncidents = activeIncidents;
    }
}
