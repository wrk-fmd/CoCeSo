package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.PasswordForm;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  public User getById(int id);

  public User getByUsername(String username);

  public User getByPersonnelId(int pid);

  public List<User> getAll();

  public Page<User> getAll(Pageable pageable, final String filter);

  public User update(User editedUser, User user);

  public boolean setPassword(int user_id, String password, User user);

  public boolean setPassword(PasswordForm form, User user);

  public boolean setActiveConcern(User user, Concern concern);

  public int importUsers(String data, User user);

}
