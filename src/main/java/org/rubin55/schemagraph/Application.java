package org.rubin55.schemagraph;

import org.rubin55.schemagraph.oracle.Dependency;
import org.rubin55.schemagraph.oracle.EvaluationContext;
import org.rubin55.schemagraph.oracle.Function;
import org.rubin55.schemagraph.oracle.IndexType;
import org.rubin55.schemagraph.oracle.Item;
import org.rubin55.schemagraph.oracle.Library;
import org.rubin55.schemagraph.oracle.MaterializedView;
import org.rubin55.schemagraph.oracle.NonExistent;
import org.rubin55.schemagraph.oracle.Operator;
import org.rubin55.schemagraph.oracle.Package;
import org.rubin55.schemagraph.oracle.PackageBody;
import org.rubin55.schemagraph.oracle.Procedure;
import org.rubin55.schemagraph.oracle.Rule;
import org.rubin55.schemagraph.oracle.RuleSet;
import org.rubin55.schemagraph.oracle.Schema;
import org.rubin55.schemagraph.oracle.Sequence;
import org.rubin55.schemagraph.oracle.Synonym;
import org.rubin55.schemagraph.oracle.Table;
import org.rubin55.schemagraph.oracle.Trigger;
import org.rubin55.schemagraph.oracle.Type;
import org.rubin55.schemagraph.oracle.TypeBody;
import org.rubin55.schemagraph.oracle.Undefined;
import org.rubin55.schemagraph.oracle.View;
import org.rubin55.schemagraph.oracle.XmlSchema;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        ObjectMapper mapper = new ObjectMapper();

        List<Item> work = new ArrayList<>();

        try {
            File jsonFile = new File("D:/Dropbox/Incoming/ras-dependencies.json");
            Dependency[] dependencies = mapper.readValue(jsonFile, Dependency[].class);
            for (Dependency dependency : dependencies) {
                log.info("Creating relation between " + dependency.name + " and " + dependency.referencedName);
                Schema owner = new Schema(dependency.owner);
                Item item = specialize(dependency.type, dependency.name);
                owner.takeOwnership(item);

                Schema referencedOwner = new Schema(dependency.referencedOwner);
                Item referencedItem = specialize(dependency.referencedType, dependency.referencedName);
                referencedOwner.takeOwnership(referencedItem);

                item.addReference(referencedItem);

                work.add(item);

            }
        } catch (Exception e) {
            log.error("Exception thrown: " + e.getMessage());
        }

        int processedItems = 0;
        int workItemTotal = work.size();
        log.info("Starting to persist item list ( with " + workItemTotal + " items in it)");

        int processedPartitions = 0;
        int partitionSize = 100;
        List<List<Item>> partitions = new ArrayList<List<Item>>();
        for (int i = 0; i < work.size(); i += partitionSize) {
            partitions.add(work.subList(i, Math.min(i + partitionSize, work.size())));
        }
        int partitionTotal = partitions.size();
        log.info("Spread work (" + workItemTotal + " items) out over " + partitionTotal + " parts");

        for (List<Item> partition : partitions) {
            processedPartitions++;
            log.info("Working on partition " + processedPartitions + " of " + partitionTotal);
            session.save(partition);
        }
    }

    public static Item specialize(String type, String name) {
        // TODO Refactor this.
        Item item = null;

        switch (type) {
        case "EVALUATION CONTXT":
            item = new EvaluationContext(name);
            break;
        case "FUNCTION":
            item = new Function(name);
            break;
        case "INDEXTYPE":
            item = new IndexType(name);
            break;
        case "LIBRARY":
            item = new Library(name);
            break;
        case "MATERIALIZED VIEW":
            item = new MaterializedView(name);
            break;
        case "NON-EXISTENT":
            item = new NonExistent(name);
            break;
        case "OPERATOR":
            item = new Operator(name);
            break;
        case "PACKAGE":
            item = new Package(name);
            break;
        case "PACKAGE BODY":
            item = new PackageBody(name);
            break;
        case "PROCEDURE":
            item = new Procedure(name);
            break;
        case "RULE":
            item = new Rule(name);
            break;
        case "RULE SET":
            item = new RuleSet(name);
            break;
        case "SEQUENCE":
            item = new Sequence(name);
            break;
        case "SYNONYM":
            item = new Synonym(name);
            break;
        case "TABLE":
            item = new Table(name);
            break;
        case "TRIGGER":
            item = new Trigger(name);
            break;
        case "TYPE":
            item = new Type(name);
            break;
        case "TYPE BODY":
            item = new TypeBody(name);
            break;
        case "UNDEFINED":
            item = new Undefined(name);
            break;
        case "VIEW":
            item = new View(name);
            break;
        case "XML SCHEMA":
            item = new XmlSchema(name);
            break;
        }
        if (item == null) {
            log.error("specialize returned null with parameters: " + type + " and " + name);
        }
        return item;
    }
}
