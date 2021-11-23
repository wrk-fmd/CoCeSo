package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.logging.ClientLog;
import at.wrk.coceso.service.LoggingService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/logging")
public class LoggingEndpoint {

    private final LoggingService loggingService;

    @Autowired
    public LoggingEndpoint(final LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public void clientLog(@RequestBody final ClientLog clientLog, @Parameter(hidden = true) final HttpServletRequest request) {
        loggingService.clientLog(clientLog, request.getRemoteHost());
    }
}
