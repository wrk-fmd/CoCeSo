package at.wrk.coceso.service.patadmin.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.repository.MedinfoRepository;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.entity.Medinfo_;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.service.patadmin.MedinfoService;
import at.wrk.coceso.specification.MedinfoSearchSpecification;
import at.wrk.coceso.utils.DataAccessLogger;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class MedinfoServiceImpl implements MedinfoService {

  private static final Logger LOG = LoggerFactory.getLogger(MedinfoServiceImpl.class);

  @Autowired
  private MedinfoRepository medinfoRepository;

  @Override
  public Medinfo getById(int id, User user) {
    Medinfo medinfo = medinfoRepository.findOne(id);
    if (medinfo == null) {
      throw new ErrorsException(Errors.EntityMissing);
    }
    if (medinfo.getConcern().isClosed()) {
      throw new ErrorsException(Errors.ConcernClosed);
    }

    DataAccessLogger.logMedinfoAccess(medinfo, user);
    return medinfo;
  }

  @Override
  public List<Medinfo> getAllByQuery(Concern concern, String query, User user) {
    query = query.trim();
    if (query.length() < 1) {
      return Collections.emptyList();
    }

    List<Medinfo> infos = medinfoRepository.findAll(new MedinfoSearchSpecification(query, concern));
    DataAccessLogger.logMedinfoAccess(infos, concern, query, user);
    return infos;
  }

  @Override
  public List<Medinfo> getForAutocomplete(Concern concern, String query, String field, User user) {
    List<Medinfo> medinfos;

    switch (field) {
      case "externalId":
        medinfos = medinfoRepository.findAll(new MedinfoSearchSpecification(query, concern) {
          @Override
          protected Predicate buildKeywordPredicate(String keyword, Root<Medinfo> root, CriteriaBuilder builder) {
            return builder.or(
                builder.like(builder.lower(root.get(Medinfo_.externalId)), keyword + "%"),
                builder.like(builder.lower(root.get(Medinfo_.externalId)), "_-" + keyword + "%")
            );
          }
        });
        break;
      case "lastname":
        medinfos = medinfoRepository.findAll(new MedinfoSearchSpecification(query, concern) {
          @Override
          protected Predicate buildKeywordPredicate(String keyword, Root<Medinfo> root, CriteriaBuilder builder) {
            return builder.like(builder.lower(root.get(Medinfo_.lastname)), keyword + "%");
          }
        });
        break;
      case "firstname":
        medinfos = medinfoRepository.findAll(new MedinfoSearchSpecification(query, concern) {
          @Override
          protected Predicate buildKeywordPredicate(String keyword, Root<Medinfo> root, CriteriaBuilder builder) {
            return builder.like(builder.lower(root.get(Medinfo_.firstname)), keyword + "%");
          }
        });
        break;
      default:
        return null;
    }

    DataAccessLogger.logMedinfoAccess(medinfos, concern, query, user);
    return medinfos;
  }

  @Override
  public int deleteAll(Concern concern) {
    return medinfoRepository.deleteByConcern(concern);
  }

  @Override
  public List<Medinfo> save(Iterable<Medinfo> medinfos) {
    LOG.info("Medical informations imported");
    return medinfoRepository.save(medinfos);
  }

}
