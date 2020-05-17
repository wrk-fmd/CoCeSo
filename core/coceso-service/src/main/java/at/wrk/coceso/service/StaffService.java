package at.wrk.coceso.service;

import at.wrk.coceso.dto.staff.StaffMemberCreateDto;
import at.wrk.coceso.dto.staff.StaffMemberDto;
import at.wrk.coceso.dto.staff.StaffMemberUpdateDto;
import at.wrk.coceso.entity.StaffMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StaffService {

    List<StaffMemberDto> getAll();

    Page<StaffMemberDto> getAll(Pageable pageable, String filter);

    StaffMemberDto create(StaffMemberCreateDto data);

    void update(StaffMember staffMember, StaffMemberUpdateDto data);

    void remove(StaffMember staffMember);

    List<StaffMemberDto> importCsv(String data);
}
