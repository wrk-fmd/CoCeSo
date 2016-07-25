package at.wrk.coceso.service.impl;

import at.wrk.coceso.auth.AuthorizationProvider;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.User_;
import at.wrk.coceso.entity.enums.AccessLevel;
import at.wrk.coceso.entity.enums.Authority;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.helper.PasswordForm;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.importer.ImportException;
import at.wrk.coceso.importer.UserImporter;
import at.wrk.coceso.repository.UserRepository;
import at.wrk.coceso.service.UserService;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class UserServiceImpl implements UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private AuthorizationProvider authorizationProvider;

  @Autowired
  private UserRepository userRepository;

  @Autowired(required = false)
  private UserImporter userImporter;

  @Override
  public User getById(int id) {
    return userRepository.findOne(id);
  }

  @Override
  public User getByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public User getByPersonnelId(int pid) {
    return userRepository.findByPersonnelId(pid);
  }

  @Override
  public List<User> getAll() {
    return userRepository.findAll();
  }

  @Override
  public Page<User> getAll(Pageable pageable, final String filter) {
    if (StringUtils.isBlank(filter)) {
      return userRepository.findAll(pageable);
    }

    Specification<User> spec = (Root<User> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
      String[] patterns = filter.trim().toLowerCase().split("(\\*|\\s|%)+");
      Predicate[] predicates = new Predicate[patterns.length];
      for (int i = 0; i < patterns.length; i++) {
        String pattern = "%" + patterns[i] + "%";
        predicates[i] = cb.or(
            cb.like(cb.lower(root.get(User_.firstname)), pattern),
            cb.like(cb.lower(root.get(User_.lastname)), pattern),
            cb.like(cb.lower(root.get(User_.username)), pattern),
            cb.like(root.get(User_.personnelId).as(String.class), pattern)
        );
      }
      return cb.and(predicates);
    };
    return userRepository.findAll(spec, pageable);
  }

  @Override
  public User update(User editedUser, User user) {
    LOG.info("{}: Triggered update of user #{}", user, editedUser.getId());

    if (editedUser.getId() != null) {
      User oldUser = getById(editedUser.getId());
      if (oldUser == null) {
        // User missing, should be checked by validator!
        throw new ErrorsException(Errors.EntityMissing);
      }

      if (!authorizationProvider.hasAccessLevel(AccessLevel.Root)) {
        // Only allow update of person data
        editedUser.setActiveConcern(oldUser.getActiveConcern());
        editedUser.setAllowLogin(oldUser.isAllowLogin());
        editedUser.setUsername(oldUser.getUsername());
        editedUser.setInternalAuthorities(oldUser.getInternalAuthorities());
      } else if (editedUser.equals(authorizationProvider.getUser())) {
        // Prevent removal of Root from own account
        editedUser.getInternalAuthorities().add(Authority.Root);
      }

      // Password and concern can only be changed through separate method
      editedUser.setHashedPW(oldUser.getPassword());
      editedUser.setActiveConcern(oldUser.getActiveConcern());
    } else {
      if (!authorizationProvider.hasAccessLevel(AccessLevel.Root)) {
        // Only allow setting of person data
        editedUser.setAllowLogin(false);
        editedUser.setUsername(null);
        editedUser.setAuthorities(null);
      }
      editedUser.setHashedPW(null);
      editedUser.setActiveConcern(null);
    }

    return userRepository.save(editedUser);
  }

  @Override
  public boolean setPassword(int user_id, String password, User user) {
    User dbUser = getById(user_id);
    if (dbUser == null) {
      // User does not exists
      LOG.warn("{}: Tried to change password of missing user #{}", user, user_id);
      return false;
    }

    if (dbUser.getUsername() == null) {
      // No active username, don't set a password
      LOG.warn("{}: Tried to change password of user #{} without username", user, user_id);
      return false;
    }

    LOG.info("{}: Changed password of user {}", user, dbUser);
    dbUser.setPassword(password);
    userRepository.save(dbUser);
    return true;
  }

  @Override
  public boolean setPassword(PasswordForm form, User user) {
    return setPassword(form.getId(), form.getPassword(), user);
  }

  @Override
  public boolean setActiveConcern(User user, Concern concern) {
    User dbUser = getById(user.getId());
    if (dbUser == null) {
      // User does not exists
      LOG.warn("Tried to change active concern of missing user #{}", user.getId());
      return false;
    }
    dbUser.setActiveConcern(concern);
    userRepository.save(dbUser);
    return true;
  }

  @Override
  public int importUsers(String data, User user) {
    if (userImporter == null) {
      LOG.warn("No user importer loaded!");
      return -1;
    }

    LOG.info("{}: started import of users", user.getUsername());
    try {
      Collection<User> updated = userImporter.updateUsers(data, getAll());
      userRepository.save(updated);
      return updated.size();
    } catch (ImportException ex) {
      return -2;
    }
  }

}
