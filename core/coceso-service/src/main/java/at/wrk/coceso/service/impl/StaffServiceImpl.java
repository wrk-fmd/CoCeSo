package at.wrk.coceso.service.impl;

import at.wrk.coceso.dto.staff.StaffMemberCreateDto;
import at.wrk.coceso.dto.staff.StaffMemberDto;
import at.wrk.coceso.dto.staff.StaffMemberUpdateDto;
import at.wrk.coceso.entity.StaffMember;
import at.wrk.coceso.exceptions.NoImporterException;
import at.wrk.coceso.exceptions.StaffImportException;
import at.wrk.coceso.mapper.StaffMapper;
import at.wrk.coceso.parser.staff.CsvParsingException;
import at.wrk.coceso.parser.staff.ParsedStaffMember;
import at.wrk.coceso.parser.staff.StaffParser;
import at.wrk.coceso.repository.StaffRepository;
import at.wrk.coceso.service.StaffService;
import at.wrk.coceso.utils.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private StaffParser staffParser;

    @Autowired
    public StaffServiceImpl(final StaffRepository staffRepository, final StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    @Autowired(required = false)
    public void setStaffParser(StaffParser staffParser) {
        this.staffParser = staffParser;
    }

    @Override
    public List<StaffMemberDto> getAll() {
        return staffRepository.findAll().stream()
                .map(staffMapper::staffMemberToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StaffMemberDto> getAll(final Pageable pageable, final String filter) {
        if (StringUtils.isBlank(filter)) {
            return staffRepository.findAll(pageable).map(staffMapper::staffMemberToDto);
        }

        // TODO Check this
        Specification<StaffMember> spec = (Root<StaffMember> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            String[] patterns = filter.trim().toLowerCase().split("(\\*|\\s|%)+");
            Predicate[] predicates = new Predicate[patterns.length];
            for (int i = 0; i < patterns.length; i++) {
                try {
                    int personnelId = Integer.parseInt(patterns[i]);
                    predicates[i] = cb.isMember(personnelId, root.get("personnelId"));
                } catch (NumberFormatException e) {
                    String pattern = "%" + patterns[i] + "%";
                    predicates[i] = cb.or(
                            cb.like(cb.lower(root.get("firstname")), pattern),
                            cb.like(cb.lower(root.get("lastname")), pattern)
                    );
                }
            }
            return cb.and(predicates);
        };
        return staffRepository.findAll(spec, pageable).map(staffMapper::staffMemberToDto);
    }

    @Override
    public StaffMemberDto create(final StaffMemberCreateDto data) {
        log.info("{}: Triggered creation of staff member with data {}", AuthenticatedUser.getName(), data);

        StaffMember staffMember = new StaffMember();
        staffMember.setFirstname(data.getFirstname());
        staffMember.setLastname(data.getLastname());
        staffMember.setInfo(data.getInfo());
        staffMember.setPersonnelId(data.getPersonnelId());
        staffMember.setContacts(staffMapper.contactDtosToContacts(data.getContacts()));

        staffMember = staffRepository.save(staffMember);
        return staffMapper.staffMemberToDto(staffMember);
    }

    @Override
    public void update(final StaffMember staffMember, final StaffMemberUpdateDto data) {
        log.info("{}: Triggered update of staff member {} with data {}", AuthenticatedUser.getName(), staffMember, data);

        if (data.getFirstname() != null) {
            staffMember.setFirstname(data.getFirstname());
        }
        if (data.getLastname() != null) {
            staffMember.setLastname(data.getLastname());
        }
        if (data.getInfo() != null) {
            staffMember.setInfo(data.getInfo());
        }
        if (data.getPersonnelId() != null) {
            staffMember.setPersonnelId(data.getPersonnelId());
        }
        if (data.getContacts() != null) {
            staffMember.setContacts(staffMapper.contactDtosToContacts(data.getContacts()));
        }
    }

    @Override
    public void remove(StaffMember staffMember) {
        log.info("{}: Triggered deletion of staff member {}", AuthenticatedUser.getName(), staffMember);
        staffRepository.delete(staffMember);
    }

    @Override
    public List<StaffMemberDto> importCsv(final String data) {
        if (staffParser == null) {
            log.warn("No staff importer loaded!");
            throw new NoImporterException("CSV importer for staff is not loaded");
        }

        log.info("{}: started import of staff", AuthenticatedUser.getName());
        try {
            Collection<ParsedStaffMember> imported = staffParser.parse(data);
            if (imported.isEmpty()) {
                return Collections.emptyList();
            }

            // An import will normally update most staff members, so it should be best to just load all existing entries together
            Collection<StaffMember> existing = staffRepository.findAll();

            Map<String, StaffMember> byExternalId = existing.stream()
                    .filter(staffMember -> staffMember.getExternalId() != null)
                    .collect(Collectors.toMap(StaffMember::getExternalId, Function.identity()));

            Map<Integer, StaffMember> byPersonnelId = new HashMap<>();
            existing.stream()
                    .filter(staffMember -> staffMember.getPersonnelId() != null)
                    .forEach(staffMember ->
                            staffMember.getPersonnelId().forEach(personnelId -> byPersonnelId.put(personnelId, staffMember))
                    );

            return imported.stream()
                    .map(item -> update(item, byExternalId, byPersonnelId))
                    .collect(Collectors.toList());
        } catch (CsvParsingException e) {
            log.warn("Exception during staff import", e);
            throw new StaffImportException(e.getMessage());
        }
    }

    private StaffMemberDto update(final ParsedStaffMember data, final Map<String, StaffMember> byExternalId,
            final Map<Integer, StaffMember> byPersonnelId) {
        StaffMember staffMember = null;

        if (data.getExternalId() != null) {
            // Try to use existing entry based on external id
            staffMember = byExternalId.get(data.getExternalId());
        }

        if (staffMember == null && data.getPersonnelId() != null) {
            // Try to use existing entry based on personnelId
            staffMember = data.getPersonnelId().stream().map(byPersonnelId::get)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        if (staffMember == null) {
            // No existing entry found, create a new one
            staffMember = new StaffMember();
        }

        if (data.getExternalId() != null) {
            staffMember.setExternalId(data.getExternalId());
        }
        if (data.getFirstname() != null) {
            staffMember.setFirstname(data.getFirstname());
        }
        if (data.getLastname() != null) {
            staffMember.setLastname(data.getLastname());
        }
        if (data.getInfo() != null) {
            staffMember.setInfo(data.getInfo());
        }
        if (data.getPersonnelId() != null) {
            staffMember.setPersonnelId(new LinkedHashSet<>(data.getPersonnelId()));
        }
        if (data.getContacts() != null) {
            staffMember.setContacts(staffMapper.contactDtosToContacts(data.getContacts()));
        }

        staffMember = staffRepository.save(staffMember);
        return staffMapper.staffMemberToDto(staffMember);
    }
}
