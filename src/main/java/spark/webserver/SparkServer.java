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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Spark server implementation
 *
 * @author Per Wendel
 */
public class SparkServer {
    private static final Logger log = LoggerFactory.getLogger(SparkServer.class);

    private static final int SPARK_DEFAULT_PORT = 4567;
    private static final String NAME = "Spark";
    private Handler handler;
    private Server server;

    public SparkServer(Handler handler) {
        this.handler = handler;
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");
    }

    /**
     * Ignites the spark server, listening on the specified port, running SSL secured with the specified keystore
     * and truststore.  If truststore is null, keystore is reused.
     *
     * @param host                  The address to listen on
     * @param port                  - the port
     * @param keystoreFile          - The keystore file location as string
     * @param keystorePassword      - the password for the keystore
     * @param truststoreFile        - the truststore file location as string, leave null to reuse keystore
     * @param truststorePassword    - the trust store password
     * @param staticFilesFolder      - the route to static files in classPath
     * @param externalFilesFolder - the route to static files external to classPath.
     */
    public void ignite(String host, int port, String keystoreFile,
                       String keystorePassword, String truststoreFile,
                       String truststorePassword, Set<String> staticFilesFolder,
                       Set<String> externalFilesFolder) {

        if (port == 0) {
            try (ServerSocket s = new ServerSocket(0)) {
                port = s.getLocalPort();
            } catch (IOException e) {
                log.warn("Could not get first available port (port set to 0), using default: {}", SPARK_DEFAULT_PORT);
                port = SPARK_DEFAULT_PORT;
            }
        }

        ServerConnector connector;

        if (keystoreFile == null) {
            connector = createSocketConnector();
        } else {
            connector = createSecureSocketConnector(keystoreFile,
                                                    keystorePassword, truststoreFile, truststorePassword);
        }

        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
        connector.setSoLingerTime(-1);
        connector.setHost(host);
        connector.setPort(port);

        server = connector.getServer();
        server.setConnectors(new Connector[] {connector});

        // Handle static file routes
        if (staticFilesFolder == null && externalFilesFolder == null) {
            server.setHandler(handler);
        } else {
            List<Handler> handlers = new ArrayList<>();
            handlers.add(handler);

            // Set static file location
            setStaticFileLocationIfPresent(staticFilesFolder, handlers);

            // Set external static file location
            setExternalStaticFileLocationIfPresent(externalFilesFolder, handlers);

            HandlerList handlersList = new HandlerList();
            handlersList.setHandlers(handlers.toArray(new Handler[handlers.size()]));
            server.setHandler(handlersList);
        }

        try {
            log.info("== " + NAME + " has ignited ..."); // NOSONAR
            log.info(">> Listening on " + host + ":" + port); // NOSONAR

            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
            System.exit(100); // NOSONAR
        }
    }

    public void stop() {
        log.info(">>> " + NAME + " is shutting down..."); // NOSONAR
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
            System.exit(100); // NOSONAR
        }
        log.info(">>> " + NAME + " is down!"); // NOSONAR
    }

    /**
     * Creates a secure jetty socket connector. Keystore required, truststore
     * optional. If truststore not specifed keystore will be reused.
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse keystore
     * @param truststorePassword the trust store password
     * @return a secure socket connector
     */
    private static ServerConnector createSecureSocketConnector(String keystoreFile,
                                                               String keystorePassword, String truststoreFile,
                                                               String truststorePassword) {

        SslContextFactory sslContextFactory = new SslContextFactory(
                keystoreFile);

        if (keystorePassword != null) {
            sslContextFactory.setKeyStorePassword(keystorePassword);
        }
        if (truststoreFile != null) {
            sslContextFactory.setTrustStorePath(truststoreFile);
        }
        if (truststorePassword != null) {
            sslContextFactory.setTrustStorePassword(truststorePassword);
        }
        return new ServerConnector(new Server(), sslContextFactory);
    }

    /**
     * Creates an ordinary, non-secured Jetty server connector.
     *
     * @return - a server connector
     */
    private static ServerConnector createSocketConnector() {
        return new ServerConnector(new Server());
    }

    /**
     * Sets static file location if present
     */
    private static void setStaticFileLocationIfPresent(Set<String> staticFilesRoutes, List<Handler> handlersInList) {
        if (staticFilesRoutes.isEmpty()) return;
        List<Resource> resources = new ArrayList<>(staticFilesRoutes.size());
        for (String staticFilesRoute : staticFilesRoutes) {
            resources.add(Resource.newClassPathResource(staticFilesRoute));
        }
        ResourceHandler resourceHandler = new ResourceHandler();
        ResourceCollection resourceCollection = new ResourceCollection(resources.toArray(new Resource[resources.size()]));
        resourceHandler.setBaseResource(resourceCollection);
        resourceHandler.setWelcomeFiles(new String[] {"index.html"});
        handlersInList.add(resourceHandler);
    }

    /**
     * Sets external static file location if present
     */
    private static void setExternalStaticFileLocationIfPresent(Set<String> externalFilesRoutes, List<Handler> handlersInList) {
        if (externalFilesRoutes.isEmpty()) return;
        List<Resource> resources = new ArrayList<>(externalFilesRoutes.size());
        for (String externalFilesRoute : externalFilesRoutes) {
            try {
                resources.add(Resource.newResource(new File(externalFilesRoute)));
            } catch (IOException e) {
                log.error("Error during initialize external resource {}", externalFilesRoute, e);
            }
        }
        ResourceHandler externalResourceHandler = new ResourceHandler();
        ResourceCollection resourceCollection = new ResourceCollection(resources.toArray(new Resource[resources.size()]));
        externalResourceHandler.setBaseResource(resourceCollection);
        externalResourceHandler.setWelcomeFiles(new String[]{"index.html"});
        handlersInList.add(externalResourceHandler);
    }

}
