package org.rubin55.schemagraph;

import org.rubin55.schemagraph.oracle.Table;
import org.rubin55.schemagraph.oracle.Schema;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("Let's do some stuff.");

        // Sane logging black magic.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final SessionFactory sessionFactory = new SessionFactory("org.rubin55.schemagraph");
        Session session = sessionFactory.openSession();

        Schema schema1 = new Schema("Rubin");
        Schema schema2 = new Schema("Rubin");

        Table table1 = new Table("SomeTable");
        Table table2 = new Table("OtherTable");

        schema1.takeOwnership(table1);
        schema2.takeOwnership(table2);
        table1.addReference(table2);

        session.save(schema1);
        session.save(schema2);

        // {"OWNER": "RUBIN", "NAME": "SomeTable", "TYPE": "TABLE", "REFERENCED_OWNER": "RUBIN", "REFERENCED_NAME": "OtherTable", "REFERENCED_TYPE": "TABLE"},
        //owner, referenced_owner : An Entity schema subtype
        //type, referenced_type : An Item subtype
        //name, referenced_name : name of item
        // for object in array do
        //   schema1 = new Schema(object.OWNER), findClassByType(object.type) -> item1 = new SubItem(object.NAME), schema1.takeOwnership(item1)
        //   schema2 = new Schema(object.REFERENCED_OWNER), findClassByType(object.type) -> item2 = new SubItem(object.REFERENCED_NAME), schema2.takeOwnership(item2)
        //   item1.addReference(item2)
        // done
    }
}
