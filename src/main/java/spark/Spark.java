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
package spark;

import spark.exception.ExceptionHandlerImpl;
import spark.exception.ExceptionMapper;
import spark.interceptor.Interceptor;
import spark.interceptor.InterceptorRegistration;
import spark.route.HttpMethod;

/**
 * The main building block of a Spark application is a set of routes. A route is
 * made up of three simple pieces:
 * <ul>
 * <li>A verb (get, post, put, delete, head, trace, connect, options)</li>
 * <li>A path (/hello, /users/:name)</li>
 * <li>A callback (request, response)</li>
 * </ul>
 * Example:
 * get("/hello", (request, response) -&#62; {
 * return "Hello World!";
 * });
 *
 * @author Per Wendel
 */
public final class Spark extends SparkBase {
    // Hide constructor
    private Spark() {
    }


    /**
     * Map the route for HTTP GET requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void get(final String path, final Route route) {
        addRoute(HttpMethod.get, wrap(path, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void post(String path, Route route) {
        addRoute(HttpMethod.post, wrap(path, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void put(String path, Route route) {
        addRoute(HttpMethod.put, wrap(path, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void patch(String path, Route route) {
        addRoute(HttpMethod.patch, wrap(path, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void delete(String path, Route route) {
        addRoute(HttpMethod.delete, wrap(path, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void head(String path, Route route) {
        addRoute(HttpMethod.head, wrap(path, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void trace(String path, Route route) {
        addRoute(HttpMethod.trace, wrap(path, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void connect(String path, Route route) {
        addRoute(HttpMethod.connect, wrap(path, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void options(String path, Route route) {
        addRoute(HttpMethod.options, wrap(path, route));
    }

    /**
     * Maps an interceptor to be executed before any matching routes
     *
     * @param path        the path
     * @param interceptor The interceptor
     * @return object for detailed interceptor configuration
     */
    public static synchronized InterceptorRegistration before(String path, Interceptor interceptor) {
        return addInterceptor(new InterceptorRegistration().before(path).execute(interceptor));
    }

    /**
     * Maps an interceptor to be executed after any matching routes
     *
     * @param path        the path
     * @param interceptor The filter
     * @return object for detailed interceptor configuration
     */
    public static synchronized InterceptorRegistration after(String path, Interceptor interceptor) {
        return addInterceptor(new InterceptorRegistration().after(path).execute(interceptor));
    }

    /**
     * Creates object to configure interceptor to be executed before any matching route
     *
     * @return object for detailed interceptor configuration
     */
    public static synchronized InterceptorRegistration before() {
        return addInterceptor(new InterceptorRegistration().before());
    }

    /**
     * Creates object to configure interceptor to be executed before any matching route
     *
     * @param pathes the pathes
     * @return object for detailed interceptor configuration
     */
    public static synchronized InterceptorRegistration before(String... pathes) {
        return addInterceptor(new InterceptorRegistration().before(pathes));
    }

    /**
     * Creates object to configure interceptor to be executed after any matching route
     *
     * @return object for detailed interceptor configuration
     */
    public static synchronized InterceptorRegistration after() {
        return addInterceptor(new InterceptorRegistration().after());
    }

    /**
     * Creates object to configure interceptor to be executed after any matching route
     *
     * @param pathes the pathes
     * @return object for detailed interceptor configuration
     */
    public static synchronized InterceptorRegistration after(String... pathes) {
        return addInterceptor(new InterceptorRegistration().after(pathes));
    }

    //////////////////////////////////////////////////
    // BEGIN route/filter mapping with accept type
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP GET requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void get(String path, String acceptType, Route route) {
        addRoute(HttpMethod.get, wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void post(String path, String acceptType, Route route) {
        addRoute(HttpMethod.post, wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void put(String path, String acceptType, Route route) {
        addRoute(HttpMethod.put, wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void patch(String path, String acceptType, Route route) {
        addRoute(HttpMethod.patch, wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void delete(String path, String acceptType, Route route) {
        addRoute(HttpMethod.delete, wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void head(String path, String acceptType, Route route) {
        addRoute(HttpMethod.head, wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void trace(String path, String acceptType, Route route) {
        addRoute(HttpMethod.trace, wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void connect(String path, String acceptType, Route route) {
        addRoute(HttpMethod.connect, wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void options(String path, String acceptType, Route route) {
        addRoute(HttpMethod.options, wrap(path, acceptType, route));
    }


    /**
     * Maps a interceptor to be executed before any matching routes
     *
     * @param interceptor The interceptor
     */
    public static synchronized InterceptorRegistration before(Interceptor interceptor) {
        return addInterceptor(new InterceptorRegistration().execute(interceptor));
    }

    /**
     * Maps a interceptor to be executed after any matching routes
     *
     * @param interceptor The interceptor
     */
    public static synchronized InterceptorRegistration after(Interceptor interceptor) {
        return addInterceptor(new InterceptorRegistration().execute(interceptor));
    }

    /**
     * Maps an interceptor to be executed before any matching routes
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param interceptor The interceptor
     */
    public static synchronized InterceptorRegistration before(String path, String acceptType, Interceptor interceptor) {
        return addInterceptor(
                new InterceptorRegistration()
                        .before(path).accepting(acceptType).execute(interceptor)
        );
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param interceptor The filter
     */
    public static synchronized InterceptorRegistration after(String path, String acceptType, Interceptor interceptor) {
        return addInterceptor(
                new InterceptorRegistration()
                        .after(path).accepting(acceptType).execute(interceptor)
        );
    }

    //////////////////////////////////////////////////
    // END route/filter mapping with accept type
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // BEGIN Template View Routes
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP GET requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void get(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.get, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void get(String path,
                                        String acceptType,
                                        TemplateViewRoute route,
                                        TemplateEngine engine) {
        addRoute(HttpMethod.get, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void post(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.post, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void post(String path,
                                         String acceptType,
                                         TemplateViewRoute route,
                                         TemplateEngine engine) {
        addRoute(HttpMethod.post, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void put(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.put, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void put(String path,
                                        String acceptType,
                                        TemplateViewRoute route,
                                        TemplateEngine engine) {
        addRoute(HttpMethod.put, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void delete(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.delete, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void delete(String path,
                                           String acceptType,
                                           TemplateViewRoute route,
                                           TemplateEngine engine) {
        addRoute(HttpMethod.delete, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void patch(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.patch, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void patch(String path,
                                          String acceptType,
                                          TemplateViewRoute route,
                                          TemplateEngine engine) {
        addRoute(HttpMethod.patch, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void head(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.head, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void head(String path,
                                         String acceptType,
                                         TemplateViewRoute route,
                                         TemplateEngine engine) {
        addRoute(HttpMethod.head, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void trace(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.trace, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void trace(String path,
                                          String acceptType,
                                          TemplateViewRoute route,
                                          TemplateEngine engine) {
        addRoute(HttpMethod.trace, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void connect(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.connect, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void connect(String path,
                                            String acceptType,
                                            TemplateViewRoute route,
                                            TemplateEngine engine) {
        addRoute(HttpMethod.connect, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void options(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.options, TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void options(String path,
                                            String acceptType,
                                            TemplateViewRoute route,
                                            TemplateEngine engine) {
        addRoute(HttpMethod.options, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    //////////////////////////////////////////////////
    // END Template View Routes
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // BEGIN Response Transforming Routes
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP GET requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void get(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.get, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void get(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.get, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void post(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.post, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void post(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.post, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void put(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.put, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void put(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.put, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void delete(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.delete, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void delete(String path,
                                           String acceptType,
                                           Route route,
                                           ResponseTransformer transformer) {
        addRoute(HttpMethod.delete, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void head(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.head, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void head(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.head, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void connect(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.connect, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void connect(String path,
                                            String acceptType,
                                            Route route,
                                            ResponseTransformer transformer) {
        addRoute(HttpMethod.connect, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void trace(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.trace, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void trace(String path,
                                          String acceptType,
                                          Route route,
                                          ResponseTransformer transformer) {
        addRoute(HttpMethod.trace, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void options(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.options, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void options(String path,
                                            String acceptType,
                                            Route route,
                                            ResponseTransformer transformer) {
        addRoute(HttpMethod.options, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void patch(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.patch, ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void patch(String path,
                                          String acceptType,
                                          Route route,
                                          ResponseTransformer transformer) {
        addRoute(HttpMethod.patch, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    //////////////////////////////////////////////////
    // END Response Transforming Routes
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // EXCEPTION mapper
    //////////////////////////////////////////////////

    /**
     * Maps an exception handler to be executed when an exception occurs during routing
     *
     * @param exceptionClass the exception class
     * @param handler        The handler
     */
    public static synchronized void exception(Class<? extends Exception> exceptionClass, ExceptionHandler handler) {
        // wrap
        ExceptionHandlerImpl wrapper = new ExceptionHandlerImpl(exceptionClass) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                handler.handle(exception, request, response);
            }
        };

        ExceptionMapper.getInstance().map(exceptionClass, wrapper);
    }

    //////////////////////////////////////////////////
    // HALT methods
    //////////////////////////////////////////////////

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     */
    public static void halt() {
        throw new HaltException();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status the status code
     */
    public static void halt(int status) {
        throw new HaltException(status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param body The body content
     */
    public static void halt(String body) {
        throw new HaltException(body);
    }

    /**
     * Immediately stops a request within a filter or route with specified status code and body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status The status code
     * @param body   The body content
     */
    public static void halt(int status, String body) {
        throw new HaltException(status, body);
    }

    //////////////////////////////////////////////////
    // model and view helper method
    //////////////////////////////////////////////////


    /**
     * Constructs a ModelAndView with the provided model and view name
     *
     * @param model    the model
     * @param viewName the view name
     * @return the model and view
     */
    public static ModelAndView modelAndView(Object model, String viewName) {
        return new ModelAndView(model, viewName);
    }

}
