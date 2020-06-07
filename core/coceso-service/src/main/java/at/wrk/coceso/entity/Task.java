package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.TaskState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Task implements Serializable {

    @EmbeddedId
    @ToString.Include
    private TaskId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("incidentId")
    private Incident incident;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("unitId")
    private Unit unit;

    @Column(nullable = false)
    private TaskState state;

    @Column(nullable = false)
    private Instant updated;

    @Column
    private Instant alarmSent;

    @Column
    private Instant casusSent;

    public Task() {
    }

    public Task(Incident incident, Unit unit, TaskState state) {
        this.id = new TaskId(incident.getId(), unit.getId());
        this.incident = incident;
        this.unit = unit;
        this.state = state;
        this.updated = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    private static class TaskId implements Serializable {

        @Column(nullable = false)
        private Long incidentId;

        @Column(nullable = false)
        private Long unitId;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TaskId)) {
                return false;
            }
            TaskId that = (TaskId) o;
            return Objects.equals(incidentId, that.incidentId) && Objects.equals(unitId, that.unitId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(incidentId, unitId);
        }
    }
}
