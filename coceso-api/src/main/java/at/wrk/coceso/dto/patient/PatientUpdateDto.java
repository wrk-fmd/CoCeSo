package at.wrk.coceso.dto.patient;

import at.wrk.coceso.dto.Lengths;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PatientUpdateDto {

    @Length(max = Lengths.PATIENT_LASTNAME)
    private String lastname;

    @Length(max = Lengths.PATIENT_FIRSTNAME)
    private String firstname;

    @Length(max = Lengths.PATIENT_EXTERNAL)
    private String externalId;

    private SexDto sex;

    @Length(max = Lengths.PATIENT_INSURANCE)
    private String insurance;

    private LocalDate birthday;

    @Length(max = Lengths.PATIENT_DIAGNOSIS)
    private String diagnosis;

    @Length(max = Lengths.PATIENT_ER_TYPE)
    private String erType;

    @Length(max = Lengths.PATIENT_INFO)
    private String info;

    public void setLastname(String lastname) {
        this.lastname = lastname != null ? lastname.trim() : null;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname != null ? firstname.trim() : null;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId != null ? externalId.trim() : null;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance != null ? insurance.trim() : null;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis != null ? diagnosis.trim() : null;
    }

    public void setErType(String erType) {
        this.erType = erType != null ? erType.trim() : null;
    }

    public void setInfo(String info) {
        this.info = info != null ? info.trim() : null;
    }
}
