package at.wrk.coceso.repository;

import at.wrk.coceso.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

  public User findByUsername(String username);

  public User findByPersonnelId(int pid);

}
