package org.rubin55.silver;

import ch.qos.logback.classic.Logger;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.LoggerFactory;

import static org.neo4j.driver.v1.Values.parameters;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

class Loader {
    private static final Logger log = (Logger) LoggerFactory.getLogger(Loader.class);
    private static Configuration cfg = Configuration.getInstance();

    private static final String entityLabel = "ENTITY";
    private static final String entityVariable = entityLabel.toLowerCase();

    private static int count = 0;
    private static int total = 0;

    public static void load() {
        log.debug("Neo4j connection string is: " + cfg.getNeo4jConnectionString());

        Driver driver = GraphDatabase.driver(cfg.getNeo4jConnectionString(),
                AuthTokens.basic(cfg.getNeo4jUser(), cfg.getNeo4jPass()));

        Session session = driver.session();

        // We don't use Cypher's LOAD CSV, as it cannot deal with labels from field values.
        // We don't use neo4j-import tool, because it's an external dependency.
        // We do however implement neo4j-import's csv format!

        try {
            List<String[]> nodes = CSVHelper.csvToList(cfg.getConfigurationPath() + "/nodes.csv");
            List<String[]> relations = CSVHelper.csvToList(cfg.getConfigurationPath() + "/relations.csv");

            // Idempotently creates constraints for csv headers suffixed with :ID
            log.info("Setting up constraints and indexes");
            String[] nodeHeaders = nodes.get(0);
            for (String nodeHeader : nodeHeaders) {
                if (nodeHeader.endsWith(":ID")) {
                    // This header represents an attribute which functions as an ID and should be unique.
                    String attribute = nodeHeader.replace(":ID", "");
                    String constraint = "CONSTRAINT ON ( " + entityVariable + ":" + entityLabel + " ) ASSERT "
                            + entityVariable + "." + attribute + " IS UNIQUE";

                    StatementResult result = session.run("CALL db.constraints()");

                    List<String> constraints = new ArrayList<String>();

                    while (result.hasNext()) {
                        Record record = result.next();
                        constraints.add(record.get("description").asString());
                    }

                    if (!constraints.contains(constraint)) {
                        log.info("Creating new constraint for \"" + attribute + "\" attribute on the \"" + entityLabel
                                + "\" label");
                        session.run("CREATE " + constraint);
                    } else {
                        log.info("Re-using existing constraint for \"" + attribute + "\" attribute on the \""
                                + entityLabel + "\" label");
                    }
                }
                if (nodeHeader.equals("name")) {
                    // This header is (extremely) common and most probably requires an index.
                    String attribute = nodeHeader;
                    String index = "INDEX ON :" + entityLabel + "(" + attribute + ")";

                    StatementResult result = session.run("CALL db.indexes()");

                    List<String> indexes = new ArrayList<String>();

                    while (result.hasNext()) {
                        Record record = result.next();
                        indexes.add(record.get("description").asString());
                    }

                    if (!indexes.contains(index)) {
                        log.info("Creating new index for \"" + attribute + "\" attribute on the \"" + entityLabel
                                + "\" label");
                        session.run("CREATE " + index);
                    } else {
                        log.info("Re-using existing index for \"" + attribute + "\" attribute on the \""
                                + entityLabel + "\" label");
                    }
                }
            }

            // Remove the header from the node list.
            nodes.remove(0);

            // Idempotently creates nodes.
            log.info("Creating nodes..");
            count = 0;
            total = nodes.size();
            nodes.stream().forEach(x -> {
                String createNodes = "MERGE (" + entityVariable + ":" + entityLabel + ":" + x[2].replaceAll("\\s+", "_")
                        + " {id:{id}, name:{name}})";
                try (Transaction tx = session.beginTransaction()) {
                    tx.run(createNodes, parameters("id", x[0], "name", x[1]));
                    tx.success();
                    count++;
                    if (count % 100 == 0) {
                        log.info("Processed " + count + "/" + total + " nodes");
                    }
                    if (count == total) {
                        log.info("Finished creation of " + total + " nodes");
                    }
                }
            });

            // Remove the header from the relation list.
            relations.remove(0);

            // Idempotently creates relations.
            log.info("Creating relationships..");
            count = 0;
            total = relations.size();
            relations.stream().forEach(x -> {
                String createRelations = "MATCH (source:" + entityLabel + " {id:{sourceId}}),(target:" + entityLabel
                        + " {id:{targetId}}) MERGE (source)-[relation:" + x[1].replaceAll("\\s+", "_")
                        + "]->(target) RETURN source.id, type(relation), target.id";
                try (Transaction tx = session.beginTransaction()) {
                    tx.run(createRelations, parameters("sourceId", x[0], "targetId", x[2]));
                    tx.success();
                    count++;
                    if (count % 100 == 0) {
                        log.info("Processed " + count + "/" + total + " relations");
                    }
                    if (count == total) {
                        log.info("Finished creation of " + total + " relations");
                    }
                }
            });

        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }

        session.close();
        driver.close();
    }
}
