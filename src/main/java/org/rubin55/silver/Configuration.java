package org.rubin55.silver;

import java.io.Console;
import java.io.File;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static final String configurationPath = System.getProperty("user.home") + File.separator + ".silver";
    private static final String configurationFile = configurationPath + File.separator + "silver.properties";

    private static final String[] supportedJdbcDrivers = {"oracle"};
    private static final String[] supportedNeo4jDrivers = {"bolt", "http"};

    private static Console console = System.console();

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

    public static void check() {
        log.debug("Invoking check routine");
    }

    public static void setup() {
        log.debug("Invoking setup routine");

        System.out.println("");

        System.out.println("*JDBC Connection setup*");

        jdbcDriver = console.readLine("Select a JDBC driver (currently only \"oracle\" is supported): ");
        jdbcName = console.readLine("Specify the database name (i.e, the Oracle SID): ");
        jdbcHost = console.readLine("Specify the database hostname: ");
        jdbcPort = console.readLine("Specify the port the database is listening on: ");
        jdbcUser = console.readLine("Specify a username to connect to the database as: ");
        jdbcPass = new String (console.readPassword("Specify a password for the above user: "));
        System.out.println("");

        System.out.println("*Neo4j Connection setup*");
        neo4jDriver = console.readLine("Select the Neo4j driver protocol (\"bolt\" or \"http\"): ");
        neo4jHost = console.readLine("Specify the Neo4j server hostname: ");
        neo4jPort = console.readLine("Specify the Neo4j port (usually \"7687\" for bolt, \"7474\" for http): ");
        neo4jUser = console.readLine("Specify a username to connect to Neo4j as: ");
        neo4jPass = new String (console.readPassword("Specify a password for the above user: "));
        System.out.println("");

        log.debug("jdbcDriver: " + jdbcDriver);
        log.debug("jdbcName: " + jdbcName);
        log.debug("jdbcHost: " + jdbcHost);
        log.debug("jdbcPort: " + jdbcPort);
        log.debug("jdbcUser: " + jdbcUser);
        log.debug("jdbcPass: " + String.join("", Collections.nCopies(jdbcPass.length(), "*")));

        log.debug("neo4jDriver: " + neo4jDriver);
        log.debug("neo4jHost: " + neo4jHost);
        log.debug("neo4jPort: " + neo4jPort);
        log.debug("neo4jUser: " + neo4jUser);
        log.debug("neo4jPass: " + String.join("", Collections.nCopies(neo4jPass.length(), "*")));

        //File file = new File(configurationFile);
        //file.getParentFile().mkdirs();
        //file.createNewFile();
    }
}
