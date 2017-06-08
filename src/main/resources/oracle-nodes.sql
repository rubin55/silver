-- nodes
-- id:ID, name, :LABEL
SELECT UNIQUE STANDARD_HASH(owner || name || type) AS "id:ID", name AS "name", type AS ":LABEL"
FROM sys.dba_dependencies
UNION
SELECT UNIQUE STANDARD_HASH(referenced_owner || referenced_name || referenced_type) AS "id:ID", referenced_name AS "name", referenced_type AS ":LABEL"
FROM sys.dba_dependencies
UNION
SELECT UNIQUE STANDARD_HASH(owner || 'SCHEMA') AS "id:ID", owner AS "name", 'SCHEMA' AS ":LABEL"
FROM sys.dba_dependencies
UNION
SELECT UNIQUE STANDARD_HASH(referenced_owner || 'SCHEMA') AS "id:ID", referenced_owner AS "name", 'SCHEMA' AS ":LABEL"
FROM sys.dba_dependencies
;
