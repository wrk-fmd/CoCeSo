package at.wrk.coceso.alarm.text.controller;

import at.wrk.coceso.alarm.text.api.export.alarm.manager.AlarmManagerExportResponse;
import at.wrk.coceso.alarm.text.api.export.alarm.manager.AlarmManagerUnit;
import at.wrk.coceso.alarm.text.service.AlarmManagerExportConverter;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.utils.ActiveConcern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data/unit/export")
@PreAuthorize("@auth.hasAccessLevel('Main')")
public class AlarmManagerExportController {

    private final AlarmManagerExportConverter alarmManagerExportConverter;

    @Autowired
    public AlarmManagerExportController(final AlarmManagerExportConverter alarmManagerExportConverter) {
        this.alarmManagerExportConverter = alarmManagerExportConverter;
    }

    @RequestMapping(value = "alarmManager", produces = "application/json", method = RequestMethod.GET)
    public AlarmManagerExportResponse getUnitsForAlarmManagerExport(@ActiveConcern Concern concern) {
        List<AlarmManagerUnit> alarmManagerUnits = alarmManagerExportConverter.getAlarmManagerUnits(concern);
        return new AlarmManagerExportResponse(alarmManagerUnits);
    }
}
