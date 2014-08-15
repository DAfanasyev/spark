package spark;

import spark.interceptor.InterceptorRegistration;
import spark.interceptor.InterceptorRegistry;
import spark.route.HttpMethod;
import spark.route.RouteEntry;
import spark.route.RouteRegistry;
import spark.route.RouteRegistryFactory;
import spark.servlet.SparkFilter;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Spark base class
 */
public abstract class SparkBase {
    public static final int SPARK_DEFAULT_PORT = 4567;
    protected static final String DEFAULT_ACCEPT_TYPE = "*/*";

    protected static boolean initialized = false;

    protected static int port = SPARK_DEFAULT_PORT;
    protected static String ipAddress = "0.0.0.0";

    protected static String keystoreFile;
    protected static String keystorePassword;
    protected static String truststoreFile;
    protected static String truststorePassword;

    protected static final Set<String> staticFileFolders = new HashSet<>();
    protected static final Set<String> externalStaticFileFolders = new HashSet<>();

    protected static SparkServer server;

    protected static RouteRegistry routeRegistry;
    protected static InterceptorRegistry interceptorRegistry;

    private static boolean runFromServlet;

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     * @deprecated replaced by {@link #ipAddress(String)}
     */
    public static synchronized void setIpAddress(String ipAddress) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        Spark.ipAddress = ipAddress;
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     */
    public static synchronized void ipAddress(String ipAddress) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        Spark.ipAddress = ipAddress;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     * @deprecated replaced by {@link #port(int)}
     */
    public static synchronized void setPort(int port) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        Spark.port = port;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public static synchronized void port(int port) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        Spark.port = port;
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     * @deprecated replaced by {@link #secure(String, String, String, String)}
     */
    public static synchronized void setSecure(String keystoreFile,
                                              String keystorePassword,
                                              String truststoreFile,
                                              String truststorePassword) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }

        if (keystoreFile == null) {
            throw new IllegalArgumentException(
                    "Must provide a keystore file to run secured");
        }

        Spark.keystoreFile = keystoreFile;
        Spark.keystorePassword = keystorePassword;
        Spark.truststoreFile = truststoreFile;
        Spark.truststorePassword = truststorePassword;
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     */
    public static synchronized void secure(String keystoreFile,
                                           String keystorePassword,
                                           String truststoreFile,
                                           String truststorePassword) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }

        if (keystoreFile == null) {
            throw new IllegalArgumentException(
                    "Must provide a keystore file to run secured");
        }

        Spark.keystoreFile = keystoreFile;
        Spark.keystorePassword = keystorePassword;
        Spark.truststoreFile = truststoreFile;
        Spark.truststorePassword = truststorePassword;
    }

    /**
     * Sets the folder in classpath serving static files. Observe: this method
     * must be called before all other methods.
     *
     * @param folder the folder in classpath.
     */
    public static synchronized void staticFileLocation(String folder) {
        if (initialized && !runFromServlet) {
            throwBeforeRouteMappingException();
        }
        staticFileFolders.add(folder);
        if (runFromServlet) {
            SparkFilter.configureStaticResources(folder);
        }
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public static synchronized void externalStaticFileLocation(String externalFolder) {
        if (initialized && !runFromServlet) {
            throwBeforeRouteMappingException();
        }
        externalStaticFileFolders.add(externalFolder);
        if (runFromServlet) {
            SparkFilter.configureExternalStaticResources(externalFolder);
        }
    }

    private static void throwBeforeRouteMappingException() {
        throw new IllegalStateException(
                "This must be done before route mapping has begun");
    }

    private static boolean hasMultipleHandlers() {
        return !staticFileFolders.isEmpty() || !externalStaticFileFolders.isEmpty();
    }

    /**
     * Stops the Spark server and clears all routes
     */
    public static synchronized void stop() {
        if (server != null) {
            routeRegistry.clearRoutes();
            interceptorRegistry.clearInterceptors();
            server.stop();
        }
        initialized = false;
    }

    static synchronized void runFromServlet() {
        runFromServlet = true;
        if (!initialized) {
            routeRegistry = RouteRegistryFactory.get();
            interceptorRegistry = InterceptorRegistry.get();
            initialized = true;
        }
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path  the path
     * @param route the route
     * @return the wrapped route
     */
    protected static RouteImpl wrap(final String path, final Route route) {
        return wrap(path, DEFAULT_ACCEPT_TYPE, route);
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      the route
     * @return the wrapped route
     */
    protected static RouteImpl wrap(final String path, String acceptType, final Route route) {
        if (acceptType == null) {
            acceptType = DEFAULT_ACCEPT_TYPE;
        }
        return new RouteImpl(path, acceptType) {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return route.handle(request, response);
            }
        };
    }

    protected static void addRoute(HttpMethod httpMethod, RouteImpl route) {
        init();
        routeRegistry.addRoute(new RouteEntry(httpMethod, route.getPath(), route.getAcceptType(), route));
    }

    protected static InterceptorRegistration addInterceptor(InterceptorRegistration registration) {
        init();
        interceptorRegistry.addInterceptor(registration);
        return registration;
    }

    private static synchronized void init() {
        if (!initialized) {
            routeRegistry = RouteRegistryFactory.get();
            interceptorRegistry = InterceptorRegistry.get();
            new Thread(() -> {
                server = SparkServerFactory.create(hasMultipleHandlers());
                server.ignite(
                        ipAddress,
                        port,
                        keystoreFile,
                        keystorePassword,
                        truststoreFile,
                        truststorePassword,
                        staticFileFolders,
                        externalStaticFileFolders);
            }).start();
            initialized = true;
        }
    }

}
