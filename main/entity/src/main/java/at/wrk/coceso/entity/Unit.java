package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.point.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Unit implements Serializable, Comparable<Unit>, ConcernBoundEntity {

    @JsonView(JsonViews.Always.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonView(JsonViews.Client.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concern_fk", updatable = false, nullable = false)
    private Concern concern;

    @JsonView({JsonViews.Main.class, JsonViews.ClientDetail.class, JsonViews.Patadmin.class})
    @Column(nullable = false)
    private UnitState state;

    @JsonView(JsonViews.Always.class)
    @Column(nullable = false, length = 64)
    @NotEmpty(message = "unit.call.notempty")
    private String call;

    @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
    @Column(nullable = false, length = 64)
    private String ani;

    @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
    @Column
    private boolean withDoc;

    @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
    @Column
    private boolean portable;

    @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
    @Column
    private boolean transportVehicle;

    @JsonView(JsonViews.Edit.class)
    @Column
    private UnitType type;

    @JsonView(JsonViews.Patadmin.class)
    @Column
    private Integer capacity;

    @JsonView(JsonViews.Patadmin.class)
    @Length(max = 30)
    @Column(length = 30)
    private String imgsrc;

    @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "crew", joinColumns = {
            @JoinColumn(name = "unit_fk")}, inverseJoinColumns = {
            @JoinColumn(name = "user_fk")})
    private Set<User> crew;

    @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
    @Column(nullable = false)
    private String info;

    @JsonView(JsonViews.Main.class)
    @Column
    private Point position;

    @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
    @Column
    private Point home;

    // TODO: FK relation!
    @JsonView({JsonViews.Edit.class, JsonViews.Main.class})
    @Column(name = "section_fk")
    private String section;

    @JsonView({JsonViews.Main.class, JsonViews.ClientDetail.class})
    @ElementCollection
    @CollectionTable(name = "task", joinColumns = {@JoinColumn(name = "unit_fk")})
    @MapKeyJoinColumn(name = "incident_fk")
    @Column(name = "state")
    private Map<Incident, TaskState> incidents;

    @JsonView({JsonViews.Main.class, JsonViews.ClientDetail.class})
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task", joinColumns = {@JoinColumn(name = "unit_fk")})
    @MapKeyColumn(name = "incident_fk")
    @Column(name = "lastStateChangeAt")
    private Map<Integer, OffsetDateTime> incidentStateChangedAtMap;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "unit_in_container", joinColumns = {
            @JoinColumn(name = "unit_fk")}, inverseJoinColumns = {
            @JoinColumn(name = "container_fk")})
    private Container container;

    @JsonView(JsonViews.Edit.class)
    @Formula("EXISTS(SELECT 1 FROM log l WHERE l.unit_fk = id AND (l.type != 'UNIT_CREATE' OR l.type IS NULL))")
    @Basic(fetch = FetchType.LAZY)
    private Boolean locked;

    public Unit() {
    }

    public Unit(int id) {
        this.id = id;
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (state == null) {
            state = UnitState.AD;
        }

        if (call == null) {
            call = "";
        }

        if (ani == null) {
            ani = "";
        }

        if (info == null) {
            info = "";
        }

        if (home != null) {
            home.tryToResolveExternalData();
        }

        if (position != null) {
            position.tryToResolveExternalData();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return (this.id != null && this.id.equals(((Unit) obj).id));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(Unit t) {
        if (t == null || t.id == null) {
            return id == null ? 0 : -1;
        }
        return id == null ? 1 : id.compareTo(t.id);
    }

    @Override
    public String toString() {
        return String.format("#%d (%s)", id, call);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Concern getConcern() {
        return concern;
    }

    public void setConcern(Concern concern) {
        this.concern = concern;
    }

    public UnitState getState() {
        return state;
    }

    public void setState(UnitState state) {
        this.state = state;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getAni() {
        return ani;
    }

    public void setAni(String ani) {
        this.ani = ani;
    }

    public boolean isWithDoc() {
        return withDoc;
    }

    public void setWithDoc(boolean withDoc) {
        this.withDoc = withDoc;
    }

    public boolean isPortable() {
        return portable;
    }

    public void setPortable(boolean portable) {
        this.portable = portable;
    }

    public boolean isTransportVehicle() {
        return transportVehicle;
    }

    public void setTransportVehicle(boolean transportVehicle) {
        this.transportVehicle = transportVehicle;
    }

    public UnitType getType() {
        return type;
    }

    public void setType(UnitType type) {
        this.type = type;
    }

    public Set<User> getCrew() {
        return crew;
    }

    public void addCrew(User user) {
        if (crew == null) {
            crew = new HashSet<>();
        }
        crew.add(user);
    }

    public void removeCrew(User user) {
        if (crew != null) {
            crew.remove(user);
        }
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Point getHome() {
        return home;
    }

    public void setHome(Point home) {
        this.home = home;
    }

    @JsonIgnore
    public Map<Incident, TaskState> getIncidents() {
        return incidents;
    }

    @JsonProperty("incidents")
    public Map<Integer, TaskState> getIncidentsSlim() {
        return incidents == null ? null : incidents.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue));
    }

    @JsonProperty("incidents")
    public void setIncidentsSlim(Map<Integer, TaskState> incidents) {
        this.incidents = incidents.entrySet().stream().collect(Collectors.toMap(entry -> new Incident(entry.getKey()), Map.Entry::getValue));
    }

    @JsonProperty(value = "incidentStateChangeTimestamps", access = JsonProperty.Access.READ_ONLY)
    public Map<Integer, OffsetDateTime> getIncidentStateChangedAtMap() {
        return incidentStateChangedAtMap;
    }

    public void addIncident(Incident incident, TaskState state) {
        if (incidents == null) {
            incidents = new HashMap<>();
        }

        if (incidentStateChangedAtMap == null) {
            incidentStateChangedAtMap = new HashMap<>();
        }

        incidents.put(incident, state);
        incidentStateChangedAtMap.put(incident.getId(), OffsetDateTime.now());
        incident.addUnit(this, state);
    }

    public void removeIncident(Incident incident) {
        if (incidents != null) {
            incidents.remove(incident);
        }

        if (incidentStateChangedAtMap != null) {
            incidentStateChangedAtMap.remove(incident.getId());
        }

        incident.removeUnit(this);
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Boolean isLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    @JsonView(JsonViews.Patadmin.class)
    public int getPatients() {
        return incidents == null ? 0 : (int) incidents.keySet().stream()
                .filter(i -> i.getType() == IncidentType.Treatment && !i.getState().isDone())
                .count();
    }
}
