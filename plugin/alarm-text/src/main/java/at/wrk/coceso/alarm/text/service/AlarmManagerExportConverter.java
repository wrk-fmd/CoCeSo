package at.wrk.coceso.alarm.text.service;

import at.wrk.coceso.alarm.text.api.export.alarm.manager.AlarmManagerUnit;
import at.wrk.coceso.alarm.text.api.export.alarm.manager.AnalogRadioContact;
import at.wrk.coceso.alarm.text.api.export.alarm.manager.SmsContact;
import at.wrk.coceso.alarm.text.api.export.alarm.manager.TetraContact;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.repository.UnitRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AlarmManagerExportConverter {

    private final UnitRepository unitRepository;
    private final UnitTargetFactory unitTargetFactory;


    @Autowired
    public AlarmManagerExportConverter(
            final UnitRepository unitRepository,
            final UnitTargetFactory unitTargetFactory) {
        this.unitRepository = unitRepository;
        this.unitTargetFactory = unitTargetFactory;
    }

    public List<AlarmManagerUnit> getAlarmManagerUnits(final Concern concern) {
        List<Unit> unitsByConcern = unitRepository.findByConcern(concern);

        return unitsByConcern.stream()
                .map(this::createAlarmManagerUnit)
                .collect(Collectors.toList());
    }

    private AlarmManagerUnit createAlarmManagerUnit(final Unit unit) {
        List<AnalogRadioContact> analogRadioIds = getAnalogRadioIds(unit);
        Map<String, List<String>> validTargetsOfUnit = unitTargetFactory.getValidTargetsOfUnitStream(Stream.of(unit));

        List<SmsContact> smsContacts = validTargetsOfUnit
                .getOrDefault("tel", List.of())
                .stream()
                .map(smsContact -> new SmsContact(unit.getCall(), smsContact))
                .collect(Collectors.toUnmodifiableList());

        List<TetraContact> tetraContacts = validTargetsOfUnit
                .getOrDefault("tetra", List.of())
                .stream()
                .map(tetraContact -> new TetraContact(unit.getCall(), tetraContact))
                .collect(Collectors.toUnmodifiableList());

        return new AlarmManagerUnit(unit.getId().toString(), unit.getCall(), analogRadioIds, smsContacts, tetraContacts);
    }

    private List<AnalogRadioContact> getAnalogRadioIds(final Unit unit) {
        List<AnalogRadioContact> analogRadioContacts = List.of();
        String ani = unit.getAni();
        if (StringUtils.isNotBlank(ani) && !ani.startsWith("tel") && !ani.startsWith("tetra")) {
            analogRadioContacts = List.of(new AnalogRadioContact(ani));
        }

        return analogRadioContacts;
    }
}
