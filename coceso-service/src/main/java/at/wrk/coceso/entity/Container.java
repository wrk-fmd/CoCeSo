package at.wrk.coceso.entity;

import at.wrk.coceso.dto.Lengths;
import lombok.Getter;
import lombok.Setter;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class Container implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, nullable = false)
    private Concern concern;

    @Column
    private long ordering;

    @ManyToOne(fetch = FetchType.EAGER)
    private Container parent;

    @Column(nullable = false, length = Lengths.CONTAINER_NAME)
    private String name;

    @OneToMany(mappedBy = "parent")
    private Set<Container> children;

    @OneToMany(mappedBy = "container")
    private Set<Unit> units;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (name == null) {
            name = "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Container)) {
            return false;
        }
        Container container = (Container) o;
        return Objects.equals(id, container.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("#%d (%s)", id, name);
    }

    public void setParent(Container parent) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        if (parent != null) {
            parent.children.add(this);
        }

        this.parent = parent;
    }

    public void clearUnits() {
        if (this.units != null) {
            this.units.forEach(Unit::removeContainer);
            this.units.clear();
        }
    }

    void addUnit(Unit unit) {
        units.add(unit);
    }

    void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public List<Long> getSortedChildren() {
        return children == null ? Collections.emptyList() : children.stream()
                .sorted(Comparator.comparing(Container::getOrdering))
                .map(Container::getId)
                .collect(Collectors.toList());
    }

    public List<Long> getSortedUnits() {
        return units == null ? Collections.emptyList() : units.stream()
                .sorted(Comparator.comparing(Unit::getOrdering))
                .map(Unit::getId)
                .collect(Collectors.toList());
    }
}
