package stivlo.house;


import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.support.DaemonLoader;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.TimeZone;

public class Application implements Daemon {

    private static final String CONFIG_FILENAME = "/startup.properties";

    private static final String APP_PORT_PROPERTY = "app.port";
    private static final String APP_NAME_PROPERTY = "app.name";
    private static final String APP_PATH_PROPERTY = "app.path";
    private static final String APP_PROFILE_PROPERTY = "app.profile";
    private static final String APP_MAX_THREADS_PROPERTY = "app.maxThreads";
    private static final String APP_IDLE_TIMEOUT_PROPERTY = "app.idleTimeout";
    private static final String APP_CONFIG_PACKAGE_PROPERTY = "app.configPackage";
    private static Logger LOG;

    private WebServer webServer;

    private Properties properties;

    // main allows to start from IDEs, however as server, it will start from init
    public static void main(String[] args) throws Exception {
        DaemonLoader.load(Application.class.getName(), args);
        DaemonLoader.start();
    }

    @Override
    public void init(DaemonContext context) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeZone.setDefault(DateTimeZone.UTC);
        LOG = LoggerFactory.getLogger(Application.class);
        loadProperties();
        LOG.info("Starting up {}...", getProperty(APP_NAME_PROPERTY));

        webServer = new WebServer();
        webServer.setConfigPackage(getProperty(APP_CONFIG_PACKAGE_PROPERTY));
        webServer.setPort(getIntProperty(APP_PORT_PROPERTY));
        webServer.setContextPath(getProperty(APP_PATH_PROPERTY));
        webServer.setProfile(getProperty(APP_PROFILE_PROPERTY));
        webServer.setMaxThreads(getIntProperty(APP_MAX_THREADS_PROPERTY));
        webServer.setIdleTimeout(getIntProperty(APP_IDLE_TIMEOUT_PROPERTY));
    }

    @Override
    public void start() throws Exception {
        webServer.startServer();
    }

    @Override
    public void stop() throws Exception {
        LOG.info("Shutting down {}...", getProperty(APP_NAME_PROPERTY));
        webServer.stopServer();
        LOG.info("{} shutdown complete", getProperty(APP_NAME_PROPERTY));
    }

    @Override
    public void destroy() {
        webServer = null;
    }

    private String getProperty(String propertyName) {
        try {
            return properties.getProperty(propertyName);
        } catch (Exception ex) {
            LOG.error("Could not read property " + propertyName);
            System.exit(1);
            return ""; // to shut up the compiler
        }
    }

    private int getIntProperty(String propertyName) {
        try {
            return Integer.parseInt(properties.getProperty(APP_PORT_PROPERTY));
        } catch (Exception ex) {
            LOG.error("Could not read property " + APP_PORT_PROPERTY);
            System.exit(1);
            return -1; // to shut up the compiler
        }
    }

    private void loadProperties() throws IOException {
        properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream(CONFIG_FILENAME)) {
            properties.load(in);
        } catch (Exception ex) {
            LOG.error("Could not load properties from " + CONFIG_FILENAME);
            System.exit(1);
        }
    }

}