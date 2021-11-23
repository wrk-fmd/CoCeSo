package at.wrk.coceso.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Setter
public class ReceivedMessage implements Serializable, Comparable<ReceivedMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column
    private String channel;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private boolean emergency;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceivedMessage)) {
            return false;
        }
        ReceivedMessage message = (ReceivedMessage) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ReceivedMessage that) {
        return this.timestamp.compareTo(that.timestamp);
    }

    @Override
    public String toString() {
        return String.format("#%d (%s: %s)", id, timestamp, sender);
    }
}
