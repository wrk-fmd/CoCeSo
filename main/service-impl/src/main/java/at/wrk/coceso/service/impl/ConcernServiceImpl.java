package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.repository.ConcernRepository;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.validator.impl.BeanValidator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class ConcernServiceImpl implements ConcernService {

  private final static Logger LOG = LoggerFactory.getLogger(ConcernServiceImpl.class);

  @Autowired
  private BeanValidator validator;

  @Autowired
  private ConcernRepository concernRepository;

  @Autowired
  private LogService logService;

  @Override
  public Concern getById(int id) {
    return concernRepository.findOne(id);
  }

  @Override
  public List<Concern> getAll() {
    return concernRepository.findAll();
  }

  @Override
  public Concern getByName(String name) {
    return concernRepository.findByName(name);
  }

  @Override
  public Concern update(Concern concern, User user) {
    Concern save = concern.getId() == null ? new Concern() : getById(concern.getId());
    if (save.isClosed()) {
      // Don't allow update of closed concern
      LOG.error("{}: Tried to update closed concern {}", user, concern);
      throw new ErrorsException(Errors.ConcernClosed);
    }

    LOG.debug("{}: Triggered update of concern {}", user, concern);
    save.setId(concern.getId());
    save.setName(concern.getName());
    save.setInfo(concern.getInfo());

    validator.validate(this);

    save = concernRepository.saveAndFlush(save);

    // TODO json
    logService.logAuto(user, concern.getId() == null ? LogEntryType.CONCERN_CREATE : LogEntryType.CONCERN_UPDATE, save, null);

    return save;
  }

  @Override
  public void setClosed(int concern_id, boolean close, User user) {
    Concern concern = getById(concern_id);
    if (concern == null) {
      throw new ErrorsException(Errors.ConcernMissing);
    }
    if (concern.isClosed() && close) {
      throw new ErrorsException(Errors.ConcernClosed);
    }
    if (!concern.isClosed() && !close) {
      throw new ErrorsException(Errors.ConcernOpen);
    }

    concern.setClosed(close);
    concernRepository.saveAndFlush(concern);

    if (close) {
      LOG.info("{}: Closed concern {}", user, concern);
      logService.logAuto(user, LogEntryType.CONCERN_CLOSE, concern, null);
    } else {
      LOG.info("{}: Reopened concern {}", user, concern);
      logService.logAuto(user, LogEntryType.CONCERN_REOPEN, concern, null);
    }
  }

  @Override
  public void addSection(String section, int concernId) {
    Concern concern = getById(concernId);
    if (Concern.isClosed(concern)) {
      throw new ErrorsException(Errors.ConcernMissingOrClosed);
    }
    if (StringUtils.isBlank(section)) {
      throw new ErrorsException(Errors.SectionEmpty);
    }
    section = section.trim();
    if (concern.containsSection(section)) {
      throw new ErrorsException(Errors.SectionExists);
    }
    concern.addSection(section);
    concernRepository.saveAndFlush(concern);
  }

  @Override
  public void removeSection(String section, int concernId) {
    // TODO: Update units and incidents!

    Concern concern = getById(concernId);
    if (Concern.isClosed(concern)) {
      throw new ErrorsException(Errors.ConcernMissingOrClosed);
    }
    concern.removeSection(section);
    concernRepository.saveAndFlush(concern);
  }

  @Override
  public boolean isClosed(Integer concernId) {
    return concernId == null || Concern.isClosed(getById(concernId));
  }

  @Override
  public boolean isClosed(Concern concern) {
    return concern == null || isClosed(concern.getId());
  }

}
