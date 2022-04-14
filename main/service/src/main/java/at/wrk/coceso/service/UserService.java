package at.wrk.coceso.service;

import at.wrk.coceso.data.AuthenticatedUser;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.PasswordForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;

@Service
public interface UserService {

  @Nullable
  User getById(int id);

  User getByUsername(String username);

  User getByPersonnelId(int personnelId);

  List<User> getAll();

  Page<User> getAll(Pageable pageable, String filter);

  User update(User editedUser);

  boolean setPassword(int userId, String password);

  boolean setPassword(PasswordForm form);

  boolean setActiveConcern(AuthenticatedUser user, Concern concern);

  int importUsers(String data);

}
