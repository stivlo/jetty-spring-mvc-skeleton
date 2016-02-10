package stivlo.house;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;

public class WebServer {

    private static final Logger LOG = LoggerFactory.getLogger(WebServer.class);

    private String configPackage;
    private String contextPath;
    private String profile;
    private int port;
    private int maxThreads;
    private int idleTimeout;

    private static final String MAPPING_URL = "/*";
    private static final String HOST = "localhost";
    private Server server;

    public void setConfigPackage(String configPackage) {
        this.configPackage = configPackage;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void startServer() throws IOException {
        LOG.info("Starting server at port {}", port);
        server = createServer(port);
        server.setHandler(getServletContextHandler());
        try {
            server.start();
            LOG.info("Server started at port {}", port);
            server.join();
        } catch (Exception ex) {
            LOG.error("Could not start server at port {}: {}", port, ex.getMessage());
            stopServer();
        }
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            LOG.error("error stopping server:", e);
        }
        System.exit(1);
    }

    private Server createServer(int port) {
        server = new Server(new QueuedThreadPool(maxThreads));
        ServerConnector http = new ServerConnector(server);
        http.setHost(HOST);
        http.setPort(port);
        http.setIdleTimeout(idleTimeout);
        server.addConnector(http);
        return server;
    }

    private ServletContextHandler getServletContextHandler() throws IOException {
        WebApplicationContext context = getContext();
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(contextPath);
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), MAPPING_URL);
        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase(new ClassPathResource("webapp").getURI().toString());
        return contextHandler;
    }

    private WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext();
        mvcContext.setConfigLocation(configPackage);
        mvcContext.getEnvironment().setDefaultProfiles(profile);
        return mvcContext;
    }

}
