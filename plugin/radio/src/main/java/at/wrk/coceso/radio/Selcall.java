package at.wrk.coceso.radio;

import at.wrk.coceso.entity.types.EnumUserType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@TypeDef(typeClass = EnumUserType.class,
        parameters = @org.hibernate.annotations.Parameter(name = "enumClass", value = "at.wrk.coceso.radio.Selcall$Direction"),
        defaultForType = Selcall.Direction.class)
public class Selcall implements Serializable, Comparable<Selcall> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 10)
    @NotEmpty
    @Column(nullable = false, length = 10)
    private String ani;

    @NotNull
    @Column(name = "ts")
    private OffsetDateTime timestamp;

    @NotNull
    @Column(nullable = false)
    private Direction direction;

    @Size(max = 20)
    @Column(length = 20)
    private String port;

    public Selcall() {
    }

    public Selcall(String port, String ani, Direction direction) {
        this.port = port;
        this.ani = ani;
        this.direction = direction;
        this.timestamp = OffsetDateTime.now();
    }

    @Override
    public int compareTo(Selcall that) {
        return this.timestamp.compareTo(that.timestamp);
    }

    public enum Direction {
        RX, RX_ACK, RX_EMG, TX, TX_FAILED
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

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
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
        return "Selcall{" +
                "id=" + id +
                ", ani='" + ani + '\'' +
                ", timestamp=" + timestamp +
                ", direction=" + direction +
                ", port='" + port + '\'' +
                '}';
    }
}
