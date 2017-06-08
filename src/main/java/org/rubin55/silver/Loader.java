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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class Loader {
    private static final Logger log = (Logger) LoggerFactory.getLogger(Loader.class);
    private static Configuration cfg = Configuration.getInstance();

    private static String entityLabel = "ENTITY";

    public static void load() {
        log.debug("Neo4j connection string is: " + cfg.getNeo4jConnectionString());

        Driver driver = GraphDatabase.driver(cfg.getNeo4jConnectionString(),
                AuthTokens.basic(cfg.getNeo4jUser(), cfg.getNeo4jPass()));

        Session session = driver.session();

        // We don't use Cypher's LOAD CSV, as it cannot deal with labels from field values.
        // We don't use neo4j-import tool, because it's an external dependency.
        // We do however implement neo4j-import's csv format!

        //Read Michaels' answer here: https://stackoverflow.com/questions/38289595/neo4j-java-bolt-create-node-is-slow-how-to-improve-it

        try {
            List<String[]> csvList = CSVHelper.csvToList(cfg.getConfigurationPath() + "/nodes.csv");

            // Idempotently creates constraints for csv headers suffixed with :ID
            // * Check for constraint existence with  CALL db.constraints();
            // * If not exist, CREATE CONSTRAINT ON (entity:ENTITY) ASSERT entity.name IS UNIQUE
            String[] headers = csvList.get(0);
            for (String header : headers) {
                if (header.endsWith(":ID")) {
                    // This header represents an attribute which functions as an ID and should be unique
                    String attribute = header.replace(":ID", "");
                    String entityVariable = entityLabel.toLowerCase();
                    String constraint = "CONSTRAINT ON ( " + entityVariable + ":" + entityLabel + " ) ASSERT "
                            + entityVariable + "." + attribute + " IS UNIQUE";

                    StatementResult result = session.run("CALL db.constraints()");

                    List<String> constraints =  new ArrayList<String>();

                    while (result.hasNext()) {
                        Record record = result.next();
                        constraints.add(record.get("description").asString());
                    }

                    if (!constraints.contains(constraint)) {
                        log.info("Creating new constraint for \"" + attribute + "\" attribute on the \"" + entityLabel + "\" label");
                        session.run("CREATE " + constraint);
                    } else {
                        log.info("Woo, found existing constraint for \"" + attribute + "\" attribute on the \"" + entityLabel + "\" label");
                    }
                }
            }

            // Idempotently inserts nodes
            // * Read line from nodes csv
            // MERGE (entity:Entity:labelFromValue {name:nameFromValue}) RETURN entity

            // Idempotently inserts relations
            // * Read line from relations csv
            // MATCH (source:Entity {name:nameFromValue}),(target:Entity {name:nameFromValue})
            // MERGE (source)-[r:typeFromValue]->(target)
            // RETURN source.name, type(r), target.name

        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }



        StatementResult result = session.run("MATCH (n) RETURN n.name AS name");

        while (result.hasNext()) {
            Record record = result.next();

            System.out.println(record.get("name").toString());
        }

        session.close();
        driver.close();
    }
}
