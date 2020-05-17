package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Patient;

interface PatientDoneHook {

    public void call(Patient patient);
}
