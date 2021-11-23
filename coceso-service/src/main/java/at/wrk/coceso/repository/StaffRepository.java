package at.wrk.coceso.repository;

import at.wrk.coceso.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<StaffMember, Long>, JpaSpecificationExecutor<StaffMember> {

}
