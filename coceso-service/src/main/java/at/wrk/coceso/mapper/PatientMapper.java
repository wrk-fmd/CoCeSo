package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.patient.PatientBriefDto;
import at.wrk.coceso.dto.patient.PatientDto;
import at.wrk.coceso.dto.patient.SexDto;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.Sex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientBriefDto patientToBriefDto(Patient patient);

    @Mapping(target = "concern", source = "concern.id")
    PatientDto patientToDto(Patient patient);

    Sex sexDtoToSex(SexDto dto);

    default String dateToString(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ISO_DATE) : null;
    }
}
