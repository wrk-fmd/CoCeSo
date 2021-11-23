package at.wrk.coceso.service;

import at.wrk.coceso.dto.logging.ClientLog;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;

import java.util.Collection;

/**
 * This service is responsible for logging specific events to files
 */
public interface LoggingService {

    void clientLog(ClientLog entry, String remoteHost);

    void logPatientAccess(final Patient patient);

    void logPatientAccess(final Collection<Patient> infos, final Concern concern);

    void logPatientAccess(final Collection<Patient> infos, final Concern concern, final String query);
}
