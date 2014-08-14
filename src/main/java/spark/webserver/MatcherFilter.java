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
package spark.webserver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.HaltException;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;
import spark.exception.ExceptionHandlerImpl;
import spark.exception.ExceptionMapper;
import spark.interceptor.InterceptorMatch;
import spark.interceptor.InterceptorRegistry;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.RouteRegistry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static spark.interceptor.InterceptorRegistration.InterceptionPhase;
import static spark.interceptor.InterceptorRegistration.InterceptionPhase.after;
import static spark.interceptor.InterceptorRegistration.InterceptionPhase.before;

/**
 * Filter for matching of filters and routes.
 *
 * @author Per Wendel
 */
public class MatcherFilter implements Filter {

    private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2>The requested route [{0}] has not been mapped in Spark</body></html>";
    private static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";

    private static final String ACCEPT_TYPE_REQUEST_MIME_HEADER = "Accept";

    private static final Logger log = LoggerFactory.getLogger(MatcherFilter.class);

    private InterceptorRegistry interceptorRegistry;
    private RouteRegistry routeRegistry;

    private boolean isServletContext;
    private boolean hasOtherHandlers;


    /**
     * Constructor
     *
     * @param routeRegistry    The route matcher
     * @param isServletContext If true, chain.doFilter will be invoked if request is not consumed by Spark.
     * @param hasOtherHandlers If true, do nothing if request is not consumed by Spark in order to let others handlers process the request.
     */
    public MatcherFilter(RouteRegistry routeRegistry, InterceptorRegistry interceptorRegistry,
                         boolean isServletContext, boolean hasOtherHandlers) {
        this.routeRegistry = routeRegistry;
        this.interceptorRegistry = interceptorRegistry;
        this.isServletContext = isServletContext;
        this.hasOtherHandlers = hasOtherHandlers;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Do nothing
    }

    @Override
    public void destroy() {
        // Do nothing
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String method = httpRequest.getMethod().toLowerCase();
        String requestUri = httpRequest.getRequestURI();
        String acceptType = httpRequest.getHeader(ACCEPT_TYPE_REQUEST_MIME_HEADER);

        log.debug(format("Request: httpMethod=''{0}'', requestUri=''{1}'', acceptType=''{2}''", method, requestUri, acceptType));

        HttpMethod httpMethod = HttpMethod.valueOf(method);

        RequestWrapper req = new RequestWrapper();
        ResponseWrapper res = new ResponseWrapper();

        RequestContext rqCtx = new RequestContext(httpMethod, requestUri, acceptType, req, res, httpRequest, httpResponse);

        try {
            executeInterceptors(before, rqCtx);

            executeRouteHandlerAndRenderResult(rqCtx);

            executeInterceptors(after, rqCtx);

        } catch (HaltException e) {

            handleHaltException(e, rqCtx);

        } catch (Exception e) {

            handleException(e, rqCtx);

        }

        String bodyContent = rqCtx.bodyContent;

        // If redirected and content is null set to empty string to not throw NotConsumedException
        if (bodyContent == null && res.isRedirected()) {
            bodyContent = StringUtils.EMPTY;
        }

        boolean consumed = bodyContent != null;

        if (!consumed && hasOtherHandlers) {
            throw new NotConsumedException();
        }

        if (!consumed && !isServletContext) {
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            bodyContent = format(NOT_FOUND, requestUri);
            consumed = true;
        }

        if (consumed) {
            if (!httpResponse.isCommitted()) {
                if (httpResponse.getContentType() == null) {
                    httpResponse.setContentType("text/html; charset=utf-8");
                }
                httpResponse.getOutputStream().write(bodyContent.getBytes("utf-8"));
            }
        } else {
            if (chain != null) {
                chain.doFilter(httpRequest, httpResponse);
            }
        }
    }

    private void executeInterceptors(InterceptionPhase phase, RequestContext rqCtx) throws Exception {
        // Finds interceptor to execute on the phase
        List<InterceptorMatch> matches = interceptorRegistry.findInterceptors(phase, rqCtx.httpMethod, rqCtx.requestUri, rqCtx.acceptType);

        for (InterceptorMatch match : matches) {
            Request request = RequestResponseFactory.create(match, rqCtx.httpReq);
            Response response = RequestResponseFactory.create(rqCtx.httpRes);

            rqCtx.reqWrapper.setDelegate(request);
            rqCtx.resWrapper.setDelegate(response);

            match.interceptor.handle(rqCtx.reqWrapper, rqCtx.resWrapper);

            rqCtx.setBodyContentFromResponse();
        }
    }

    private void executeRouteHandlerAndRenderResult(RequestContext rqCtx) throws Exception {
        // Finds route handler to execute and render result
        RouteMatch match = routeRegistry.findTargetForRequestedRoute(rqCtx.httpMethod, rqCtx.requestUri, rqCtx.acceptType);

        if (match != null) {
            if (match.route != null) {
                rqCtx.reqWrapper.setDelegate(RequestResponseFactory.create(match, rqCtx.httpReq));
                rqCtx.resWrapper.setDelegate(RequestResponseFactory.create(rqCtx.httpRes));

                Object handlerResult = match.route.handle(rqCtx.reqWrapper, rqCtx.resWrapper);
                String renderedBodyContent = match.route.render(handlerResult);

                rqCtx.setBodyContentNotNull(renderedBodyContent);
            }
        } else {
            // The HEAD method is identical to GET except that the server
            // MUST NOT return a message-body in the response.
            // If GET is mapped return same headers and empty body.
            if (rqCtx.httpMethod == HttpMethod.head && rqCtx.bodyContent == null) {
                RouteMatch getMatch = routeRegistry.findTargetForRequestedRoute(HttpMethod.get, rqCtx.requestUri, rqCtx.acceptType);
                // Use empty body to make request consumed
                rqCtx.bodyContent = getMatch != null ? StringUtils.EMPTY : null;
            }
        }
    }

    private void handleHaltException(HaltException exception, RequestContext rqCtx) {
        log.debug(format("Halt occurred: statusCode=''{0}''", exception.getStatusCode()));
        rqCtx.httpRes.setStatus(exception.getStatusCode());
        rqCtx.setBodyContentOrEmpty(exception.getBody());
    }

    private void handleException(Exception exception, RequestContext rqCtx) {
        ExceptionHandlerImpl handler = ExceptionMapper.getInstance().getHandler(exception);
        if (handler != null) {
            handler.handle(exception, rqCtx.reqWrapper, rqCtx.resWrapper);
            rqCtx.setBodyContentFromResponse();
        } else {
            log.error(StringUtils.EMPTY, exception);
            rqCtx.httpRes.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            rqCtx.bodyContent = INTERNAL_ERROR;
        }
    }

    /**
     * Context object to hold all necessary staff to run request through interceptors and route handler
     */
    private static class RequestContext {
        public final HttpMethod httpMethod;
        public final String requestUri;
        public final String acceptType;
        public final RequestWrapper reqWrapper;
        public final ResponseWrapper resWrapper;
        public final HttpServletRequest httpReq;
        public final HttpServletResponse httpRes;

        public String bodyContent;

        private RequestContext(HttpMethod httpMethod, String requestUri, String acceptType,
                               RequestWrapper reqWrapper, ResponseWrapper resWrapper,
                               HttpServletRequest httpReq, HttpServletResponse httpRes) {
            this.httpMethod = httpMethod;
            this.requestUri = requestUri;
            this.acceptType = acceptType;
            this.reqWrapper = reqWrapper;
            this.resWrapper = resWrapper;
            this.httpReq = httpReq;
            this.httpRes = httpRes;
        }

        public void setBodyContentNotNull(String bodyContent) {
            this.bodyContent = defaultString(bodyContent, this.bodyContent);
        }

        public void setBodyContentOrEmpty(String bodyContent) {
            this.bodyContent = defaultString(bodyContent, StringUtils.EMPTY);
        }

        public void setBodyContentFromResponse() {
            setBodyContentNotNull(resWrapper.getDelegate().body());
        }
    }
}
