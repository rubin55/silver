package org.rubin55.schemagraph;

import org.rubin55.schemagraph.oracle.Table;
import org.rubin55.schemagraph.oracle.Schema;

import ch.qos.logback.classic.Level;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {
        log.info("Let's do some stuff.");

        // Sane logging.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        final SessionFactory sessionFactory = new SessionFactory("org.rubin55.schemagraph");
        Session session = sessionFactory.openSession();


        Schema schema = new Schema("Rubin");

        Table table1 = new Table("SomeTable");
        Table table2 = new Table("OtherTable");

        schema.takeOwnership(table1);
        schema.takeOwnership(table2);
        table1.addReference(table2);

        session.save(schema);
    }
}
