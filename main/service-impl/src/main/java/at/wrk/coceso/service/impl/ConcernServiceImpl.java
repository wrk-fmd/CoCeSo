package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.repository.ConcernRepository;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.utils.AuthenicatedUserProvider;
import at.wrk.coceso.validator.impl.BeanValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

  private final AuthenicatedUserProvider authenicatedUserProvider;

  @Autowired
  ConcernServiceImpl(final AuthenicatedUserProvider authenicatedUserProvider) {
    this.authenicatedUserProvider = authenicatedUserProvider;
  }

  @Override
  public Concern getById(int id) {
    return concernRepository.findOne(id);
  }

  @Override
  public List<Concern> getAll() {
    return concernRepository.findAll();
  }

  @Override
  public List<Concern> getAllOpen() {
    return concernRepository.findAllOpen();
  }

  @Override
  public Concern getByName(String name) {
    return concernRepository.findByName(name);
  }

  @Override
  public Concern update(Concern concern) {
    Concern save = concern.getId() == null ? new Concern() : getById(concern.getId());
    if (save.isClosed()) {
      // Don't allow update of closed concern
      LOG.error("Tried to update an already closed concern {}", concern);
      throw new ErrorsException(Errors.ConcernClosed);
    }

    LOG.debug("{}: Triggered update of concern {}", authenicatedUserProvider.getAuthenticatedUser(), concern);
    save.setId(concern.getId());
    save.setName(concern.getName());
    save.setInfo(concern.getInfo());

    validator.validate(this);

    save = concernRepository.saveAndFlush(save);

    // TODO json
    logService.logAuto(concern.getId() == null ? LogEntryType.CONCERN_CREATE : LogEntryType.CONCERN_UPDATE, save, null);

    return save;
  }

  @Override
  public void setClosed(final int concernId, final boolean close) {
    Concern concern = getById(concernId);
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
      LOG.info("{}: Closed concern {}", authenicatedUserProvider.getAuthenticatedUser(), concern);
      logService.logAuto(LogEntryType.CONCERN_CLOSE, concern, null);
    } else {
      LOG.info("{}: Reopened concern {}", authenicatedUserProvider.getAuthenticatedUser(), concern);
      logService.logAuto(LogEntryType.CONCERN_REOPEN, concern, null);
    }
  }

  @Override
  public void addSection(String section, int concernId) {
    Concern concern = getById(concernId);
    if (Concern.isClosedOrNull(concern)) {
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
  public void removeSection(final String section, final int concernId) {
    // TODO: Update units and incidents!

    Concern concern = getById(concernId);
    if (Concern.isClosedOrNull(concern)) {
      throw new ErrorsException(Errors.ConcernMissingOrClosed);
    }
    concern.removeSection(section);
    concernRepository.saveAndFlush(concern);
  }

  @Override
  public boolean isClosed(Integer concernId) {
    return concernId == null || Concern.isClosedOrNull(getById(concernId));
  }

  @Override
  public boolean isClosed(Concern concern) {
    return concern == null || isClosed(concern.getId());
  }

}
