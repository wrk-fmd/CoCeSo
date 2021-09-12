package at.wrk.coceso.entity;

import at.wrk.coceso.dto.Lengths;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.entity.point.Point;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Entity
@Getter
@Setter
public class Unit implements Serializable, Comparable<Unit> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, nullable = false)
    private Concern concern;

    @Column(nullable = false)
    private UnitState state;

    @Column(nullable = false, length = Lengths.UNIT_CALL)
    private String call;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Contact> contacts;

    @Column
    private boolean withDoc;

    @Column
    private boolean portable;

    @Column
    private boolean transportVehicle;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UnitType> types;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<StaffMember> crew;

    @Column(nullable = false, length = Lengths.UNIT_INFO)
    private String info;

    @Column
    private Point position;

    @Column
    private Point home;

    // TODO: FK relation!
    @Column(length = Lengths.SECTION_NAME)
    private String section;

    @OneToMany(mappedBy = "unit")
    private Set<Task> incidents;

    @ManyToOne(fetch = FetchType.LAZY)
    private Container container;

    @Column
    private Integer ordering;

//    @Formula("EXISTS(SELECT 1 FROM log l WHERE l.unit_fk = id AND (l.type != 'UNIT_CREATE' OR l.type IS NULL))")
//    @Basic(fetch = FetchType.LAZY)
//    private Boolean locked;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (state == null) {
            state = UnitState.OFF_DUTY;
        }

        if (call == null) {
            call = "";
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
        if (!(o instanceof Unit)) {
            return false;
        }
        Unit unit = (Unit) o;
        return Objects.equals(id, unit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Unit other) {
        return new CompareToBuilder().append(this.call, other.call).toComparison();
    }

    @Override
    public String toString() {
        return String.format("#%d (%s)", id, call);
    }

    public Set<UnitType> getTypes() {
        return types != null ? types : Collections.emptySet();
    }

    public Set<StaffMember> getCrew() {
        return crew != null ? crew : Collections.emptySet();
    }

    public void addCrew(StaffMember member) {
        if (crew == null) {
            crew = new HashSet<>();
        }
        crew.add(member);
    }

    public void removeCrew(StaffMember member) {
        if (crew != null) {
            crew.remove(member);
        }
    }

    public Set<Contact> getContacts() {
        return contacts != null ? contacts : Collections.emptySet();
    }

    public Set<Task> getIncidents() {
        return incidents != null ? incidents : Collections.emptySet();
    }

    public Optional<Task> getTask(Incident incident) {
        return incidents.stream().filter(t -> t.getIncident().equals(incident)).findFirst();
    }

    public void addTask(Task task) {
        if (incidents == null) {
            incidents = new HashSet<>();
        }
        incidents.add(task);
        task.getIncident().addTask(task);
    }

    public void removeTask(Task task) {
        if (incidents != null) {
            incidents.remove(task);
        }
        task.getIncident().removeTask(task);
    }

    public void setContainer(Container container) {
        if (this.container != null) {
            this.container.removeUnit(this);
        }
        if (container != null) {
            container.addUnit(this);
        }

        this.container = container;
    }

    void removeContainer() {
        // This may only be triggered by the container and should not propagate
        container = null;
    }
}
