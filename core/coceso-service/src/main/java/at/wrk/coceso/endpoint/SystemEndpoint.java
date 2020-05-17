package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.system.SystemInfoDto;
import at.wrk.coceso.dto.system.SystemTimeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/system")
public class SystemEndpoint {

    private final BuildProperties buildProperties;

    @Autowired
    public SystemEndpoint(final BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @PreAuthorize("permitAll")
    @GetMapping
    public SystemInfoDto getVersion() {
        return new SystemInfoDto(buildProperties.getVersion());
    }

    @PreAuthorize("permitAll")
    @GetMapping("time")
    public SystemTimeDto getSystemTime() {
        return new SystemTimeDto(Instant.now());
    }
}
