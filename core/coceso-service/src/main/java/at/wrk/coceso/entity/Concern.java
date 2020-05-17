package at.wrk.coceso.entity;

import at.wrk.coceso.dto.Lengths;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
public class Concern implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = Lengths.CONCERN_NAME, unique = true)
    private String name;

    @Column(nullable = false, length = Lengths.CONCERN_INFO)
    private String info;

    @Column
    private boolean closed;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sections", joinColumns = @JoinColumn(name = "concern_fk"))
    @Column(name = "name", length = Lengths.SECTION_NAME)
    private Set<String> sections;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (info == null) {
            info = "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Concern)) {
            return false;
        }
        Concern concern = (Concern) o;
        return Objects.equals(id, concern.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("#%d (%s)", id, name);
    }

    public boolean containsSection(final String section) {
        return sections != null && sections.contains(section);
    }

    public void addSection(final String section) {
        if (sections == null) {
            sections = new HashSet<>();
        }
        sections.add(section);
    }

    public void removeSection(final String section) {
        if (sections != null) {
            sections.remove(section);
        }
    }
}
