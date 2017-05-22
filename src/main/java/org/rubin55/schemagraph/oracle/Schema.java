package org.rubin55.schemagraph.oracle;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;
import org.rubin55.schemagraph.oracle.Item;

public class Schema extends Entity {

    @Relationship(type= "OWNS", direction="OUTGOING")
    private Set<Item> owns = new HashSet<>();

    public Schema() {
        super();
    }
    public Schema(String name) {
        super(name);
    }
    public void takeOwnership(Item item) {
        owns.add(item);
        item.setOwner(this);
    }
}
