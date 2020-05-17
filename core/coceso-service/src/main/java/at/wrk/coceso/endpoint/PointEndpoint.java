package at.wrk.coceso.endpoint;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/points")
public class PointEndpoint {

    private final PointService pointService;

    @Autowired
    public PointEndpoint(final PointService pointService) {
        this.pointService = pointService;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "poiAutocomplete", produces = "application/json", method = RequestMethod.GET)
    public Collection<String> poiAutocomplete(@RequestParam("q") final String searchQuery, final Concern concern) {
        return pointService.autocomplete(searchQuery, concern);
    }
}
