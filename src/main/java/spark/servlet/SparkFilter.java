/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.servlet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Access;
import spark.interceptor.InterceptorRegistry;
import spark.resource.AbstractFileResolvingResource;
import spark.resource.AbstractResourceHandler;
import spark.resource.ClassPathResource;
import spark.resource.ClassPathResourceHandler;
import spark.resource.ExternalResource;
import spark.resource.ExternalResourceHandler;
import spark.route.RouteRegistryFactory;
import spark.webserver.MatcherFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter that can be configured to be used in a web.xml file.
 * Needs the init parameter 'applicationClass' set to the application class where
 * the adding of routes should be made.
 *
 * @author Per Wendel
 */
public class SparkFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(SparkFilter.class);

    public static final String APPLICATION_CLASS_PARAM = "applicationClass";

    private static List<AbstractResourceHandler> staticResourceHandlers = null;

    private String filterPath;
    private MatcherFilter matcherFilter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Access.runFromServlet();

        final SparkApplication application = getApplication(filterConfig);
        application.init();

        filterPath = FilterTools.getFilterPath(filterConfig);
        matcherFilter = new MatcherFilter(RouteRegistryFactory.get(), InterceptorRegistry.get(), true, false);
    }

    @Override
    public void destroy() {
        // Do nothing
    }

    /**
     * Returns an instance of {@link SparkApplication} which on which {@link SparkApplication#init() init()} will be called.
     * Default implementation looks up the class name in the filterConfig using the key {@link #APPLICATION_CLASS_PARAM}.
     * Subclasses can override this method to use different techniques to obtain an instance (i.e. dependency injection).
     *
     * @param filterConfig the filter configuration for retrieving parameters passed to this filter.
     * @return the spark application containing the configuration.
     * @throws ServletException if anything went wrong.
     */
    protected SparkApplication getApplication(FilterConfig filterConfig) throws ServletException {
        try {
            String applicationClassName = filterConfig.getInitParameter(APPLICATION_CLASS_PARAM);
            Class<?> applicationClass = Class.forName(applicationClassName);
            return (SparkApplication) applicationClass.newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
            IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request; // NOSONAR

        final String relativePath = FilterTools.getRelativePath(httpRequest, filterPath);

        if (log.isDebugEnabled()) {
            log.debug(relativePath);
        }

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getRequestURI() {
                return relativePath;
            }
        };

        // handle static resources
        if (staticResourceHandlers != null) {
            for (AbstractResourceHandler staticResourceHandler : staticResourceHandlers) {
                AbstractFileResolvingResource resource = staticResourceHandler.getResource(httpRequest);
                if (resource != null && resource.isReadable()) {
                    IOUtils.copy(resource.getInputStream(), response.getWriter());
                    return;
                }
            }
        }

        matcherFilter.doFilter(requestWrapper, response, chain);
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public synchronized static void configureStaticResources(String folder) {
        if (folder == null) return;
        try {
            ClassPathResource resource = new ClassPathResource(folder);
            if (resource.getFile().isDirectory()) {
                if (staticResourceHandlers == null) {
                    staticResourceHandlers = new ArrayList<>();
                }
                staticResourceHandlers.add(new ClassPathResourceHandler(folder, "index.html"));
                log.info("StaticResourceHandler configured with folder = " + folder);
            } else {
                log.error("Static resource location must be a folder");
            }
        } catch (IOException e) {
            log.error("Error when creating StaticResourceHandler", e);
        }
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public synchronized static void configureExternalStaticResources(String folder) {
        if (folder == null) return;
        try {
            ExternalResource resource = new ExternalResource(folder);
            if (resource.getFile().isDirectory()) {
                if (staticResourceHandlers == null) {
                    staticResourceHandlers = new ArrayList<>();
                }
                staticResourceHandlers.add(new ExternalResourceHandler(folder, "index.html"));
                log.info("External StaticResourceHandler configured with folder = " + folder);
            } else {
                log.error("External Static resource location must be a folder");
            }
        } catch (IOException e) {
            log.error("Error when creating external StaticResourceHandler", e);
        }
    }

}
