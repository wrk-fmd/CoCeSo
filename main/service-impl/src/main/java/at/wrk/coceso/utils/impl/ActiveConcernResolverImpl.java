package at.wrk.coceso.utils.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.exceptions.ConcernException;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.ActiveConcernResolver;
import at.wrk.coceso.utils.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

// TODO SMELL: This class returns different types based on the argument. This should be split into multiple classes, reading the concern from a common provider.
@Component
class ActiveConcernResolverImpl implements ActiveConcernResolver {

    private static final Set<Class<?>> SUPPORTED_PARAMETER_TYPES = Set.of(Concern.class, Integer.class, int.class);

    @Autowired
    private ConcernService concernService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterAnnotation(ActiveConcern.class) != null && SUPPORTED_PARAMETER_TYPES.contains(parameter.getParameterType());
    }

    @Override
    @Transactional
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory) throws ConcernException {
        boolean required = parameter.getParameterAnnotation(ActiveConcern.class).required();

        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        Cookie cookie = WebUtils.getCookie(servletRequest, "concern");
        if (cookie == null) {
            if (required) {
                throw new ConcernException("Active concern cookie does not exist");
            }

            return null;
        }

        int concernId;
        try {
            concernId = Integer.parseInt(cookie.getValue());
        } catch (NumberFormatException e) {
            if (required) {
                throw new ConcernException("Could not parse active concern cookie", e);
            }

            return null;
        }

        if (concernId <= 0) {
            if (required) {
                throw new ConcernException("ConcernId is invalid");
            }

            return null;
        }

        Concern concern = concernService.getById(concernId);
        if (concern == null || concern.isClosed()) {
            if (required) {
                throw new ConcernException("Concern does not exist or is closed");
            }

            return null;
        }

        if (parameter.getParameterType() == Concern.class) {
            if (parameter.getParameterAnnotation(ActiveConcern.class).sections()) {
                Initializer.init(concern, Concern::getSections);
            }

            return concern;
        }

        return concern.getId();
    }
}
