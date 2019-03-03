package at.wrk.coceso.plugin.geobroker.loader;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.repository.ConcernRepository;
import at.wrk.coceso.repository.IncidentRepository;
import at.wrk.coceso.utils.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Component
public class IncidentLoader {
    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private ConcernRepository concernRepository;

    @Transactional
    public Collection<Incident> loadAllIncidentsOfActiveConcerns() {
        Collection<Concern> openConcerns = this.concernRepository.findAllOpen();
        return openConcerns.stream()
                .map(incidentRepository::findByConcern)
                .flatMap(Collection::stream)
                .map(unit -> Initializer.init(unit, Incident::getConcern, Incident::getUnits))
                .collect(toList());
    }
}
