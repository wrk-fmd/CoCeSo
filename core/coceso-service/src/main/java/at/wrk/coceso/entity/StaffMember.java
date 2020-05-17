package at.wrk.coceso.entity;

import at.wrk.coceso.dto.Lengths;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
public class StaffMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = Lengths.STAFF_EXTERNAL_ID)
    private String externalId;

    @Column(nullable = false, length = Lengths.STAFF_FIRSTNAME)
    private String firstname;

    @Column(nullable = false, length = Lengths.STAFF_LASTNAME)
    private String lastname;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> personnelId;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Contact> contacts;

    @Column(nullable = false, length = Lengths.STAFF_INFO)
    private String info;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (firstname == null) {
            firstname = "";
        }
        if (lastname == null) {
            lastname = "";
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
        if (!(o instanceof StaffMember)) {
            return false;
        }
        StaffMember staffMember = (StaffMember) o;
        return Objects.equals(id, staffMember.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("#%d: %s %s", id, lastname, firstname);
    }

    public void addContact(Contact contact) {
        if (contacts == null) {
            contacts = new LinkedHashSet<>();
        }
        contacts.add(contact);
    }

    public boolean removeContact(Contact contact) {
        return contacts != null && contacts.remove(contact);
    }
}
