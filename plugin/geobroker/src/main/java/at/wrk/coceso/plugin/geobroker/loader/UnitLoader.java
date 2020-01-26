package at.wrk.coceso.plugin.geobroker.loader;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.repository.ConcernRepository;
import at.wrk.coceso.repository.UnitRepository;
import at.wrk.coceso.utils.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Component
public class UnitLoader {
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ConcernRepository concernRepository;

    @Transactional
    public Collection<Unit> loadAllUnitsOfActiveConcerns() {
        Collection<Concern> openConcerns = this.concernRepository.findAllOpen();
        return openConcerns.stream()
                .map(unitRepository::findByConcern)
                .flatMap(Collection::stream)
                .map(unit -> Initializer.init(unit, Unit::getIncidents, Unit::getConcern, Unit::getIncidentStateChangedAtMap))
                .collect(toList());
    }
}
