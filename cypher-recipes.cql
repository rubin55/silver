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
MATCH (n) WITH DISTINCT labels(n) AS temp, count(n) AS tempCnt UNWIND temp AS type
RETURN type, sum(tempCnt) AS count ORDER BY count DESC

// Find a schema by name and traverse its relations.
MATCH (sourceSchema:SCHEMA {name:"FOO"})-[:OWNS]->(objectOwnedBySource)-[:REFERENCES*..]->(objectOwnedByTarget)<-[:OWNS]-(targetSchema:SCHEMA)
WHERE sourceSchema.name <> targetSchema.name
RETURN sourceSchema,objectOwnedBySource,objectOwnedByTarget,targetSchema

// Show count of object types owned per schema, ignore PUBLIC and SYS.
MATCH (object)<-[:OWNS]-(schema:SCHEMA)
WHERE NOT schema.name="PUBLIC" AND NOT schema.name="SYS"
WITH DISTINCT filter(x IN (labels(object)) WHERE NOT x="ENTITY") AS temp,
schema.name AS schema,
COUNT(object) AS tempCnt
UNWIND temp AS type RETURN schema,type, SUM(tempCnt) AS count ORDER BY count DESC

// Find (indirect) relationships (dependencies) from a schema to any other schema (and remove PUBLIC + SYS from dependsOn).
MATCH (sourceSchema:SCHEMA)-[:OWNS]->(objectOwnedBySource)-[:REFERENCES*..]->(objectOwnedByTarget)<-[:OWNS]-(targetSchema:SCHEMA)
WHERE sourceSchema.name <> targetSchema.name
WITH sourceSchema, targetSchema,
filter(x IN (collect(distinct targetSchema.name)) WHERE NOT x="PUBLIC" AND NOT x="SYS")[0] AS dependsOn
ORDER BY targetSchema.name
WHERE dependsOn <> []
RETURN DISTINCT sourceSchema.name AS schema,
collect(distinct dependsOn)
ORDER BY sourceSchema.name

// Find (indirect) relationships (dependencies) from a schema to any other schema and show object from+to information.
MATCH (sourceSchema:SCHEMA)-[:OWNS]->(objectOwnedBySource)-[:REFERENCES*..]->(objectOwnedByTarget)<-[:OWNS]-(targetSchema:SCHEMA)
WHERE sourceSchema.name <> targetSchema.name
WITH sourceSchema, targetSchema, objectOwnedBySource, objectOwnedByTarget,
filter(x IN (labels(objectOwnedBySource)) WHERE NOT x="ENTITY")[0] AS sourceType,
filter(x IN (labels(objectOwnedByTarget)) WHERE NOT x="ENTITY")[0] AS targetType
RETURN DISTINCT sourceSchema.name AS sourceSchema,
objectOwnedBySource.name AS sourceObject,
sourceType,
targetSchema.name AS targetSchema,
objectOwnedByTarget.name AS targetObject,
targetType,
count(*) AS references
ORDER BY sourceSchema,targetSchema

// Create a list of schemas with a complexity indication by counting objects of a certain type.
MATCH
  (schema:SCHEMA)-[:OWNS]->(object)
WITH
  schema.name as schema,
  object.name as object,
  filter(label IN (labels(object))
    WHERE label="FUNCTION"
    OR label="PACKAGE"
    OR label="PROCEDURE"
    OR label="TABLE"
    OR label="TRIGGER"
    OR label="TYPE"
    OR label="VIEW"
  ) AS temp UNWIND temp AS type
RETURN
  schema,
  count(object) AS score
ORDER BY
  score DESC


// Show business domain to schema relations.
// Note: requires you to load and relate DOMAIN type objects in addition to objects loaded from the database.
MATCH (sourceDomain:DOMAIN)-[:USES]->(objectOwnedBySource)-[:REFERENCES*..]->(objectOwnedByTarget)<-[:OWNS]-(targetSchema:SCHEMA)
WITH sourceDomain, targetSchema,
filter(x IN (collect(distinct targetSchema.name)) WHERE NOT x="PUBLIC" AND NOT x="SYS")[0] AS dependsOn
ORDER BY targetSchema.name
WHERE dependsOn <> []
RETURN DISTINCT sourceDomain.name AS domain,
collect(distinct dependsOn) AS schemas
ORDER BY sourceDomain.name

// Show schema to business domain relations.
// Note: requires you to load and relate DOMAIN type objects in addition to objects loaded from the database.
MATCH (sourceSchema:SCHEMA)-[:OWNS]->(objectOwnedBySource)-[:REFERENCES*..]->(objectOwnedByTarget)<-[:USES]-(targetDomain:DOMAIN)
WITH sourceSchema, targetDomain,
filter(x IN (collect(distinct targetDomain.name)) WHERE NOT x="PUBLIC" AND NOT x="SYS")[0] AS dependsOn
ORDER BY sourceSchema.name
WHERE dependsOn <> []
RETURN DISTINCT sourceSchema.name AS schema,
collect(distinct dependsOn) AS domains
ORDER BY sourceSchema.name
