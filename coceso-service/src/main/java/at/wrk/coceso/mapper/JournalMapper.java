package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.journal.JournalEntryDto;
import at.wrk.coceso.entity.JournalEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface JournalMapper {

    @Mapping(target = "incident", source = "incident.id")
    @Mapping(target = "unit", source = "unit.id")
    @Mapping(target = "patient", source = "patient.id")
    JournalEntryDto journalEntryToDto(JournalEntry journalEntry);

    default List<JournalEntryDto> journalEntriesToDto(Collection<JournalEntry> entries) {
        return entries.stream()
                .map(this::journalEntryToDto)
                .collect(Collectors.toList());
    }
}
