package at.wrk.coceso.entity;

import at.wrk.coceso.dto.Lengths;
import at.wrk.coceso.entity.enums.IncidentClosedReason;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.point.Point;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
public class Incident implements Serializable, Comparable<Incident> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, nullable = false)
    private Concern concern;

    @Column
    private boolean priority;

    @Column
    private boolean blue;

    @OneToMany(mappedBy = "incident")
    private Set<Task> units;

    @Column
    private Point bo;

    @Column
    private Point ao;

    @Column(nullable = false, length = Lengths.INCIDENT_CASUS)
    private String casusNr;

    @Column(nullable = false, length = Lengths.INCIDENT_INFO)
    private String info;

    @Column(nullable = false, length = Lengths.INCIDENT_CALLER)
    private String caller;

    @Column
    private IncidentClosedReason closed;

    @Column(nullable = false)
    private IncidentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    // TODO: FK relation!
    @Column(name = "section_fk")
    private String section;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant created;

    @Column
    private Instant arrival;

    /**
     * The last state change of any task of this incident. If a task is still assigned, this timestamp matches the most recent timestamp of
     * all assigned tasks.
     * If no task is assigned anymore, it saves the time the last unit was detached.
     */
    @Column
    private Instant stateChange;

    @Column
    private Instant ended;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (casusNr == null) {
            casusNr = "";
        }

        if (caller == null) {
            caller = "";
        }

        if (info == null) {
            info = "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Incident)) {
            return false;
        }
        Incident incident = (Incident) o;
        return Objects.equals(id, incident.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(final Incident other) {
        return new CompareToBuilder().append(this.id, other.id).toComparison();
    }

    @Override
    public String toString() {
        return String.format("#%d", id);
    }

    void addTask(Task task) {
        if (units == null) {
            units = new LinkedHashSet<>();
        }
        units.add(task);
    }

    void removeTask(Task task) {
        if (units != null) {
            units.remove(task);
        }
    }

    @Deprecated
    public boolean hasAo() {
        return !Point.isEmpty(ao);
    }

    @Deprecated
    public void setArrival() {
        arrival = Instant.now();
    }

    @Deprecated
    public void setStateChange() {
        stateChange = Instant.now();
    }

    @Deprecated
    public void setEnded() {
        ended = Instant.now();
    }

    @Deprecated
    public boolean isRelevant() {
        return type == IncidentType.Task
                || type == IncidentType.Transport
                || type == IncidentType.Position;
    }
}
