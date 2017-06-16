package org.rubin55.silver;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

enum Configuration {
    INSTANCE;

    private static final Logger log = (Logger) LoggerFactory.getLogger(Configuration.class);

    private static final String configurationPath = System.getProperty("user.home") + File.separator + ".silver";
    private static final String configurationFile = configurationPath + File.separator + "silver.properties";

    private static File file = new File(configurationFile);
    private static Properties properties = new Properties();

    private static final String[] supportedJdbcDrivers = { "oracle" };
    private static final String[] supportedNeo4jDrivers = { "bolt", "http" };

    private static Console console = System.console();

    private static boolean initialized = false;

    // JDBC parameters.
    private static String jdbcDriver;
    private static String jdbcName;
    private static String jdbcHost;
    private static String jdbcPort;
    private static String jdbcUser;
    private static String jdbcPass;

    // Neo4j parameters.
    private static String neo4jDriver;
    private static String neo4jHost;
    private static String neo4jPort;
    private static String neo4jUser;
    private static String neo4jPass;

    public static Configuration getInstance() {
        init();
        return Configuration.INSTANCE;
    }

    public static void check() {
        log.info("Configuration check not implemented yet");
    }

    private static String starString(int n){
        char[] chars = new char[n];
        Arrays.fill(chars, '*');
        return new String(chars);
    }

    private static void init() {
        if (!initialized) {
            try {
                log.debug("Attempting to load properties from " + configurationFile);
                InputStream is = new FileInputStream(file);
                properties.load(is);

                jdbcDriver = properties.getProperty("jdbc.driver");
                jdbcName = properties.getProperty("jdbc.name");
                jdbcHost = properties.getProperty("jdbc.host");
                jdbcPort = properties.getProperty("jdbc.port");
                jdbcUser = properties.getProperty("jdbc.user");
                jdbcPass = properties.getProperty("jdbc.pass");

                neo4jDriver = properties.getProperty("neo4j.driver");
                neo4jHost = properties.getProperty("neo4j.host");
                neo4jPort = properties.getProperty("neo4j.port");
                neo4jUser = properties.getProperty("neo4j.user");
                neo4jPass = properties.getProperty("neo4j.pass");

                initialized = true;
            } catch (IOException e) {
                log.error(e.getMessage());
                log.info("You can create a configuration file for silver by running the \"setup\" procedure");
            }
        }
    }

    public static void setup() {
        log.debug("Invoking setup routine");

        if (file.exists()) {
            init(); // We init so we have initialized values.
            log.info("File exists: " + configurationFile);
            log.info("Please remove the configuration file if you want to re-setup.");
        } else {
            System.out.println("");

            System.out.println("*JDBC Connection setup*");

            jdbcDriver = console.readLine("Select a JDBC driver (currently only \"oracle\" is supported): ");
            jdbcName = console.readLine("Specify the database name (i.e, the Oracle SID): ");
            jdbcHost = console.readLine("Specify the database hostname: ");
            jdbcPort = console.readLine("Specify the port the database is listening on (usually \"1521\"): ");
            jdbcUser = console.readLine("Specify a username to connect to the database as: ");
            jdbcPass = new String(console.readPassword("Specify a password for the above user: "));
            System.out.println("");

            System.out.println("*Neo4j Connection setup*");
            neo4jDriver = console.readLine("Select the Neo4j driver protocol (\"bolt\" or \"http\"): ");
            neo4jHost = console.readLine("Specify the Neo4j server hostname: ");
            neo4jPort = console.readLine("Specify the Neo4j port (usually \"7687\" for bolt, \"7474\" for http): ");
            neo4jUser = console.readLine("Specify a username to connect to Neo4j as: ");
            neo4jPass = new String(console.readPassword("Specify a password for the above user: "));
            System.out.println("");

            log.debug("jdbcDriver: " + jdbcDriver);
            log.debug("jdbcName: " + jdbcName);
            log.debug("jdbcHost: " + jdbcHost);
            log.debug("jdbcPort: " + jdbcPort);
            log.debug("jdbcUser: " + jdbcUser);
            log.debug("jdbcPass: " + starString(jdbcPass.length()));

            log.debug("neo4jDriver: " + neo4jDriver);
            log.debug("neo4jHost: " + neo4jHost);
            log.debug("neo4jPort: " + neo4jPort);
            log.debug("neo4jUser: " + neo4jUser);
            log.debug("neo4jPass: " + starString(jdbcPass.length()));

            try {
                log.debug("Creating new configuration file: " + configurationFile);
                file.getParentFile().mkdirs();
                file.createNewFile();

                properties.setProperty("jdbc.driver", jdbcDriver);
                properties.setProperty("jdbc.name", jdbcName);
                properties.setProperty("jdbc.host", jdbcHost);
                properties.setProperty("jdbc.port", jdbcPort);
                properties.setProperty("jdbc.user", jdbcUser);
                properties.setProperty("jdbc.pass", jdbcPass);

                properties.setProperty("neo4j.driver", neo4jDriver);
                properties.setProperty("neo4j.host", neo4jHost);
                properties.setProperty("neo4j.port", neo4jPort);
                properties.setProperty("neo4j.user", neo4jUser);
                properties.setProperty("neo4j.pass", neo4jPass);

                OutputStream out = new FileOutputStream(file);
                properties.store(out, "silver.properties - Configuration file for silver.");
                log.info("Wrote configuration to " + configurationFile);

                out.close();

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        String sqlScriptForNodes = jdbcDriver + "-nodes.sql";
        String sqlScriptForRelations = jdbcDriver + "-relations.sql";

        copyFromClassPathToConfigDir(sqlScriptForNodes);
        copyFromClassPathToConfigDir(sqlScriptForRelations);
    }

    public static InputStream openFromConfigDir(String file) {
        log.debug("Attempting to open " + file + " from configuration directory");
        InputStream is = null;
        try {
            is = new FileInputStream(configurationPath + File.separator + file);
        } catch (FileNotFoundException e) {
            log.error("Sorry, unable to find " + file);
        }
        return is;
    }

    public static InputStream openFromClassPath(String path) {
        String resource;

        char c = path.charAt(0);

        if (c == '/') {
            resource = path;
        } else {
            resource = "/" + path;
        }

        log.debug("Attempting to open " + resource + " from classpath");
        InputStream is = Configuration.class.getResourceAsStream(resource);
        if (is == null) {
            log.error("Sorry, unable to find " + resource);
        }
        return is;
    }

    public static void copyFromClassPathToConfigDir(String fileName) {
        File file = new File(configurationPath + File.separator + fileName);

        if (!file.exists()) {
            try {
                InputStream inputStream = Configuration.openFromClassPath(fileName);
                log.info("Creating " + fileName + " from template in classpath");
                Files.copy(inputStream, file.toPath());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.info(configurationPath + File.separator + fileName + " found, not overwriting");
        }
    }

    public String getJdbcConnectionString() {
        return "jdbc:" + jdbcDriver + ":thin:@" + jdbcHost + ":" + jdbcPort + ":" + jdbcName;
    }

    public String getNeo4jConnectionString() {
        return neo4jDriver + "://" + neo4jHost + ":" + neo4jPort;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getJdbcName() {
        return jdbcName;
    }

    public String getJdbcHost() {
        return jdbcHost;
    }

    public String getJdbcPort() {
        return jdbcPort;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPass() {
        return jdbcPass;
    }

    public String getNeo4jDriver() {
        return neo4jDriver;
    }

    public String getNeo4jHost() {
        return neo4jHost;
    }

    public String getNeo4jPort() {
        return neo4jPort;
    }

    public String getNeo4jUser() {
        return neo4jUser;
    }

    public String getNeo4jPass() {
        return neo4jPass;
    }

    public String getSqlScriptForNodes() {
        return jdbcDriver + "-nodes.sql";
    }

    public String getSqlScriptForRelations() {
        return jdbcDriver + "-relations.sql";
    }

    public String getCsvFileForNodes() {
        return jdbcDriver + "-nodes.csv";
    }

    public String getCsvFileForRelations() {
        return jdbcDriver + "-relations.csv";
    }
}
