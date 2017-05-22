package org.rubin55.schemagraph.oracle;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public abstract class Item {

    @GraphId
    private Long id;

    @Index(unique=true, primary=true)
    private String name;

    @Relationship(type= "OWNS", direction="INCOMING")
    private Schema owner;

    @Relationship(type= "REFERENCES", direction="OUTGOING")
    private Set<Item> references = new HashSet<>();

    public Item() {
    }

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Schema getOwner() {
        return this.owner;
    }

    public void setOwner(Schema owner) {
        this.owner = owner;
    }

    public Set<Item> getReferences() {
        return this.references;
    }

    public void setReferences(Set<Item> references) {
        this.references = references;
    }

    public void addReference(Item item) {
        references.add(item);
    }
}
