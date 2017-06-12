// Find a node labeled SCHEMA, with attribute "name" set to FOO.
MATCH (n:SCHEMA {name:"FOO"})
RETURN n

// Show number of dangling nodes.
MATCH (n)
WHERE NOT (n)-[]-()
RETURN count(n)

// Show number of nodes without OWNS relation.
MATCH (n)
WHERE NOT (n)<-[:OWNS]-()
RETURN count(n)

// Show number of nodes without REFERENCES relation.
MATCH (n)
WHERE NOT (n)<-[:REFERENCES]-()
RETURN count(n)

// Total destruction, will delete all nodes and relations.
MATCH (n) DETACH DELETE n

// Show count of objects per label.
MATCH (n) WITH DISTINCT LABELS(n) AS temp, COUNT(n) AS tempCnt UNWIND temp AS type RETURN type, SUM(tempCnt) AS count ORDER BY type

// Find a schema by name and traverse its relations.
MATCH (sourceSchema:SCHEMA {name:"FOO"})-[:OWNS]->(objectOwnedBySource)-[:REFERENCES*..]->(objectOwnedByTarget)<-[:OWNS]-(targetSchema:SCHEMA)
WHERE sourceSchema.name <> targetSchema.name
RETURN sourceSchema,objectOwnedBySource,objectOwnedByTarget,targetSchema

// Show count of object types owned per schema.
MATCH (sourceSchema:SCHEMA)-[:OWNS]->(objectOwnedBySource)
RETURN DISTINCT sourceSchema.name AS schema,
filter(x IN (labels(objectOwnedBySource)) WHERE NOT x="ENTITY")[0] AS type,
count(*) AS count ORDER BY schema,type

// Find (indirect) relationships (dependencies) from a schema to any other schema, ignore PUBLIC and SYS.
MATCH (sourceSchema:SCHEMA)-[:OWNS]->(objectOwnedBySource)-[:REFERENCES*..]->(objectOwnedByTarget)<-[:OWNS]-(targetSchema:SCHEMA)
WHERE sourceSchema.name <> targetSchema.name
AND NOT sourceSchema.name="PUBLIC"
AND NOT targetSchema.name="PUBLIC"
AND NOT sourceSchema.name="SYS"
AND NOT targetSchema.name="SYS"
WITH sourceSchema, targetSchema
ORDER BY targetSchema.name
RETURN sourceSchema.name AS schema,
collect(distinct targetSchema.name) AS dependsOn
ORDER BY sourceSchema.name

// Find (indirect) relationships (dependencies) from a schema to any other schema and show object types + reference count, ignore PUBLIC and SYS.
MATCH (sourceSchema:SCHEMA)-[:OWNS]->(objectOwnedBySource)-[:REFERENCES*..]->(objectOwnedByTarget)<-[:OWNS]-(targetSchema)
WHERE sourceSchema <> targetSchema
AND NOT sourceSchema.name="PUBLIC"
AND NOT targetSchema.name="PUBLIC"
AND NOT sourceSchema.name="SYS"
AND NOT targetSchema.name="SYS"
RETURN DISTINCT sourceSchema.name AS schema,
filter(x IN (labels(objectOwnedBySource)) WHERE NOT x="ENTITY")[0] AS type,
count(*) AS references
ORDER BY schema,type,references

// Find (direct) relationships (dependencies) from a schema to any other schema and show object from+to information, ignore PUBLIC and SYS.
MATCH (sourceSchema:SCHEMA)-[:OWNS]->(objectOwnedBySource)-[:REFERENCES]->(objectOwnedByTarget)<-[:OWNS]-(targetSchema)
WHERE sourceSchema <> targetSchema
AND NOT sourceSchema.name="PUBLIC"
AND NOT targetSchema.name="PUBLIC"
AND NOT sourceSchema.name="SYS"
AND NOT targetSchema.name="SYS"
RETURN DISTINCT sourceSchema.name AS source,
objectOwnedBySource.name AS sourceObject,
filter(x IN (labels(objectOwnedBySource)) WHERE NOT x="ENTITY")[0] AS sourceType,
targetSchema.name AS target,
objectOwnedByTarget.name AS targetObject,
filter(x IN (labels(objectOwnedByTarget)) WHERE NOT x="ENTITY")[0] AS targetType
ORDER BY source,sourceObject,sourceType,target,targetObject,targetType
