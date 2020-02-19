package at.wrk.coceso.utils;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DataAccessLogger {
    private final static Logger LOG = LoggerFactory.getLogger(DataAccessLogger.class);

    private final AuthenicatedUserProvider authenicatedUserProvider;

    @Autowired
    public DataAccessLogger(final AuthenicatedUserProvider authenicatedUserProvider) {
        this.authenicatedUserProvider = authenicatedUserProvider;
    }

    public void logPatientAccess(final Patient patient) {
        LOG.info("{}: Reading patient information of patient '{}'", authenicatedUserProvider.getAuthenticatedUser(), patient);
    }

    public void logPatientAccess(final Collection<Patient> infos, final Concern concern) {
        LOG.info("{}: Loaded patients for concern '{}'. {} patients matched.", authenicatedUserProvider.getAuthenticatedUser(), concern, infos == null ? -1 : infos.size());
    }

    public void logPatientAccess(final Collection<Patient> infos, final Concern concern, final String query) {
        LOG.info("{}: Searching patients for query '{}' in concern '{}'. {} patients matched.", authenicatedUserProvider.getAuthenticatedUser(), query, concern, infos == null ? -1 : infos.size());
    }
}
