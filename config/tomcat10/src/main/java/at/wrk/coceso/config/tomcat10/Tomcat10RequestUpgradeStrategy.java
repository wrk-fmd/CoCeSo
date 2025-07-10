/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * The ugliest backport imaginable:
 * The doUpgrade method has been removed in Tomcat 10.1, so we need to backport this change:
 * https://github.com/spring-projects/spring-framework/commit/95395b53d5900612cc49c06da4b5581b0697cf84
 *
 * At the same time, all the other classes from spring-websocket 5 use javax, so we can't compile
 * it against Tomcat 10 libraries (which use jakarta)
 */

package at.wrk.coceso.config.tomcat10;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import org.apache.tomcat.websocket.server.WsServerContainer;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.standard.AbstractStandardUpgradeStrategy;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * A WebSocket {@code RequestUpgradeStrategy} for Apache Tomcat. Compatible with
 * Tomcat 10 and higher.
 *
 * <p>To modify properties of the underlying {@link javax.websocket.server.ServerContainer}
 * you can use {@link ServletServerContainerFactoryBean} in XML configuration or,
 * when using Java configuration, access the container instance through the
 * "javax.websocket.server.ServerContainer" ServletContext attribute.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class Tomcat10RequestUpgradeStrategy extends AbstractStandardUpgradeStrategy {

    @Override
    public String[] getSupportedVersions() {
        return new String[] {"13"};
    }

    @Override
    public void upgradeInternal(ServerHttpRequest request, ServerHttpResponse response,
        @Nullable String selectedProtocol, List<Extension> selectedExtensions, Endpoint endpoint)
        throws HandshakeFailureException {

        HttpServletRequest servletRequest = getHttpServletRequest(request);
        HttpServletResponse servletResponse = getHttpServletResponse(response);

        StringBuffer requestUrl = servletRequest.getRequestURL();
        String path = servletRequest.getRequestURI();  // shouldn't matter
        Map<String, String> pathParams = Collections.<String, String> emptyMap();

        ServerEndpointRegistration endpointConfig = new ServerEndpointRegistration(path, endpoint);
        endpointConfig.setSubprotocols(Collections.singletonList(selectedProtocol));
        endpointConfig.setExtensions(selectedExtensions);

        try {
            getContainer(servletRequest).upgradeHttpToWebSocket(servletRequest, servletResponse, endpointConfig, pathParams);
        }
        catch (Exception ex) {
            throw new HandshakeFailureException(
                "Servlet request failed to upgrade to WebSocket: " + requestUrl, ex);
        }
    }

    @Override
    public WsServerContainer getContainer(HttpServletRequest request) {
        return (WsServerContainer) super.getContainer(request);
    }

}
