package at.wrk.coceso.plugin.geobroker.controller;

import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.plugin.geobroker.contract.qr.ExternalUnit;
import at.wrk.coceso.plugin.geobroker.data.CachedUnit;
import at.wrk.coceso.plugin.geobroker.manager.GeoBrokerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/geo/unit")
public class GeobrokerUnitController {
    private static final Logger LOG = LoggerFactory.getLogger(GeobrokerUnitController.class);
    private static final String EXTERNAL_UNITS_PROPERTY = "externalUnits";

    private final GeoBrokerManager geoBrokerManager;

    @Autowired
    public GeobrokerUnitController(final GeoBrokerManager geoBrokerManager) {
        this.geoBrokerManager = geoBrokerManager;
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @RequestMapping(value = "allExternalUnits", produces = "application/json", method = RequestMethod.GET)
    public RestResponse getAllExternalUnits(@RequestParam(value = "concernId") final int concernId) {
        List<ExternalUnit> externalUnits = geoBrokerManager
                .getAllUnitsOfConcern(concernId)
                .stream()
                .map(this::createExternalUnit)
                .collect(Collectors.toList());

        LOG.debug("Returning {} external units for concern ID {}.", externalUnits.size(), concernId);
        return new RestResponse(true, new RestProperty(EXTERNAL_UNITS_PROPERTY, externalUnits));
    }

    private ExternalUnit createExternalUnit(final CachedUnit unit) {
        return new ExternalUnit(
                unit.getUnitId(),
                unit.getUnitType(),
                unit.getUnit().getName(),
                unit.getGeoBrokerUnitId(),
                unit.getUnit().getToken());
    }
}
