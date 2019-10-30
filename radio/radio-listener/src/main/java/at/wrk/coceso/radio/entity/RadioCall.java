package at.wrk.coceso.radio.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;

@Entity
// TODO Check how this should be implemented
//@TypeDef(typeClass = EnumUserType.class,
//        parameters = @org.hibernate.annotations.Parameter(name = "enumClass", value = "at.wrk.coceso.radio.entity.RadioCall$Direction"),
//        defaultForType = RadioCall.Direction.class)
public class RadioCall implements Serializable, Comparable<RadioCall> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 7)
    private String ani;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private Direction direction;

    @Column(length = 20)
    private String port;

    public RadioCall() {
    }

    @Override
    public int compareTo(RadioCall that) {
        return this.timestamp.compareTo(that.timestamp);
    }

    public enum Direction {
        RX, RX_EMG, TX
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAni() {
        return ani;
    }

    public void setAni(String ani) {
        this.ani = ani;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("ani", ani)
                .append("timestamp", timestamp)
                .append("direction", direction)
                .append("port", port)
                .toString();
    }
}
