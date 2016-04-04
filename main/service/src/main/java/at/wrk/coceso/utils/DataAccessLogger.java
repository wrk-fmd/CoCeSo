package at.wrk.coceso.utils;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataAccessLogger {

  private final static Logger LOG = LoggerFactory.getLogger(DataAccessLogger.class);

  public static void logMedinfoAccess(Medinfo info, User user) {
    LOG.info("{}: Reading medical information {}", user, info);
  }

  public static void logMedinfoAccess(Collection<Medinfo> infos, Concern concern, String query, User user) {
    LOG.info("{}: Searching medical information for query '{}' in concern '{}', matched {}", user, query, concern, infos);
  }

  public static void logPatientAccess(Patient patient, User user) {
    LOG.info("{}: Reading patient information {}", user, patient);
  }

  public static void logPatientAccess(Collection<Patient> infos, Concern concern, User user) {
    LOG.info("{}: Loaded patients for concern '{}', matched {}", user, concern, infos);
  }

  public static void logPatientAccess(Collection<Patient> infos, Concern concern, String query, User user) {
    LOG.info("{}: Searching patients for query '{}' in concern '{}', matched {}", user, query, concern, infos);
  }

}
