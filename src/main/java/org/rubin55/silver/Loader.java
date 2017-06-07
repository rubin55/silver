package org.rubin55.silver;

import ch.qos.logback.classic.Logger;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.slf4j.LoggerFactory;

import static org.neo4j.driver.v1.Values.parameters;

class Loader {
    private static final Logger log = (Logger) LoggerFactory.getLogger(Loader.class);
    private static Configuration cfg = Configuration.getInstance();

    public static void load() {
        log.debug("Invoking load routine");
        log.debug("Neo4j connection string is: " + cfg.getNeo4jConnectionString());

        //Don't use LOAD CSV, as it cannot deal with labels from field values.
        //Don't use neo4j-import tool, because it's an external dependency.

        //Read Michaels' answer here: https://stackoverflow.com/questions/38289595/neo4j-java-bolt-create-node-is-slow-how-to-improve-it

        // Implement custom CSV reader which:
        // Inserts nodes
        // Creates indexes
        // Inserts relations

        Driver driver = GraphDatabase.driver(cfg.getNeo4jConnectionString(),
                AuthTokens.basic(cfg.getNeo4jUser(), cfg.getNeo4jPass()));
        Session session = driver.session();

        StatementResult result = session.run("MATCH (n) RETURN n.name AS name");

        while (result.hasNext()) {
            Record record = result.next();

            System.out.println(record.get("name").toString());
        }

        session.close();
        driver.close();
    }
}
