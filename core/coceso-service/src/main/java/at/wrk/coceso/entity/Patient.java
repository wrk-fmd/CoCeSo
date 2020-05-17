package at.wrk.coceso.entity;

import at.wrk.coceso.dto.Lengths;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.Sex;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class Patient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, nullable = false)
    private Concern concern;

    @Column(nullable = false, length = Lengths.PATIENT_LASTNAME)
    private String lastname;

    @Column(nullable = false, length = Lengths.PATIENT_FIRSTNAME)
    private String firstname;

    @Column(nullable = false, length = Lengths.PATIENT_EXTERNAL)
    private String externalId;

    @Column
    private Sex sex;

    @Column(nullable = false, length = Lengths.PATIENT_INSURANCE)
    private String insurance;

    @Column
    private LocalDate birthday;

    @Column(nullable = false, length = Lengths.PATIENT_DIAGNOSIS)
    private String diagnosis;

    @Column(nullable = false, length = Lengths.PATIENT_ER_TYPE)
    private String erType;

    @Column(nullable = false, length = Lengths.PATIENT_INFO)
    private String info;

    @Column
    private boolean done;

    @OneToMany(mappedBy = "patient")
    private Set<Incident> incidents;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (lastname == null) {
            lastname = "";
        }
        if (firstname == null) {
            firstname = "";
        }
        if (externalId == null) {
            externalId = "";
        }
        if (insurance == null) {
            insurance = "";
        }
        if (diagnosis == null) {
            diagnosis = "";
        }
        if (erType == null) {
            erType = "";
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
        if (!(o instanceof Patient)) {
            return false;
        }
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return StringUtils.isNotBlank(externalId)
                ? String.format("#%d: %s %s (%s)", id, lastname, firstname, externalId)
                : String.format("#%d: %s %s", id, lastname, firstname);
    }

    public Set<String> getHospital() {
        if (incidents == null) {
            return null;
        }

        return incidents.stream()
                .filter(i -> i.getType() == IncidentType.Transport && i.hasAo())
                .map(i -> i.getAo().getInfo())
                .collect(Collectors.toSet());
    }

    public boolean isTransport() {
        if (incidents == null) {
            return false;
        }

        return incidents.stream().anyMatch(i -> i.getType() == IncidentType.Transport);
    }
}
