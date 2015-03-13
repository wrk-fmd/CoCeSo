package at.wrk.coceso.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Robert on 14.06.2014.
 */
@Entity
public class Selcall implements Serializable, Comparable<Selcall> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 5)
    private String ani;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar timestamp;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Transient
    private String port;

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

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
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
}
