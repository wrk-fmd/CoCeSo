package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.JournalEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.journal.Change;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "journal")
@Immutable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry implements Serializable, Comparable<JournalEntry> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Concern concern;

    @Column(nullable = false)
    @CreationTimestamp
    private Instant timestamp;

    @ManyToOne
    private Unit unit;

    @ManyToOne
    private Incident incident;

    @ManyToOne
    private Patient patient;

    @Column
    private TaskState state;

    @Column(nullable = false)
    private JournalEntryType type;

    @Column
    private String username;

    @Column
    private String text;

    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<Change> changes;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JournalEntry)) {
            return false;
        }
        JournalEntry journalEntry = (JournalEntry) o;
        return Objects.equals(id, journalEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(JournalEntry other) {
        return new CompareToBuilder().append(this.timestamp, other.timestamp).toComparison();
    }
}
