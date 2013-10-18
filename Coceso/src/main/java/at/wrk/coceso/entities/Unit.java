package at.wrk.coceso.entities;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "units")
public class Unit {
    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    Case aCase;

    @Enumerated(EnumType.STRING)
    UnitState state;

    @Basic
    String call;

    @Basic
    boolean withDoc;

    @Basic
    boolean portable;

    @Basic
    boolean transportVehicle;

    @ManyToMany
    List<Person> crew;

    @Basic
    String info;

    @ManyToMany
    @JoinColumn
    CocesoPOI position;


}
