package org.rubin55.schemagraph.oracle;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public abstract class Entity {

    @GraphId
    private Long id;

    @Index(unique=true, primary=true)
    private String name;
    public Entity() {
    }

    public Entity(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
