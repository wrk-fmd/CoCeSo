package at.wrk.coceso.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoggingAccessDeniedHandler extends AccessDeniedHandlerImpl {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingAccessDeniedHandler.class);

    @Override
    public void handle(final HttpServletRequest request,
            final HttpServletResponse response,
            final AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOG.trace("Access failed", accessDeniedException);
        super.handle(request, response, accessDeniedException);
    }
}
