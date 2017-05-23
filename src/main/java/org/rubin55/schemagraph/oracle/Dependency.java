package org.rubin55.schemagraph.oracle;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dependency {

    @JsonProperty("OWNER")
    public String owner;

    @JsonProperty("NAME")
    public String name;

    @JsonProperty("TYPE")
    public String type;

    @JsonProperty("REFERENCED_OWNER")
    public String referencedOwner;

    @JsonProperty("REFERENCED_NAME")
    public String referencedName;

    @JsonProperty("REFERENCED_TYPE")
    public String referencedType;
}
