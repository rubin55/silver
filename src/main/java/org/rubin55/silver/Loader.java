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

        Driver driver = GraphDatabase.driver(cfg.getNeo4jConnectionString(), AuthTokens.basic(cfg.getNeo4jUser(), cfg.getNeo4jPass()));
        Session session = driver.session();


        //StatementResult result = session.run("MATCH (a:Person) WHERE a.name = {name} " +
        //                                    "RETURN a.name AS name, a.title AS title",
        //                                    parameters( "name", "Arthur" ) );

        StatementResult result = session.run("MATCH (n) RETURN n");

        while (result.hasNext()) {
            Record record = result.next();
            System.out.println(record.asMap().toString());
        }

        session.close();
        driver.close();
    }
}
