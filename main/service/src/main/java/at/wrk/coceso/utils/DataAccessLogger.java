package at.wrk.coceso.utils;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public abstract class DataAccessLogger {

    private final static Logger LOG = LoggerFactory.getLogger(DataAccessLogger.class);

    public static void logPatientAccess(final Patient patient, final User user) {
        LOG.info("{}: Reading patient information of patient {}", user, patient);
    }

    public static void logPatientAccess(final Collection<Patient> infos, final Concern concern, final User user) {
        LOG.info("{}: Loaded patients for concern '{}', {} patients matched.", user, concern, infos == null ? -1 : infos.size());
    }

    public static void logPatientAccess(final Collection<Patient> infos, final Concern concern, final String query, final User user) {
        LOG.info("{}: Searching patients for query '{}' in concern '{}', {} patients matched.", user, query, concern, infos == null ? -1 : infos.size());
    }
}
