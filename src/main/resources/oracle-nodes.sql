-- nodes
-- id:ID, name, :LABEL
SELECT UNIQUE STANDARD_HASH(owner || object_name || object_type) AS "id:ID", object_name AS "name", object_type AS ":LABEL"
FROM sys.dba_objects
UNION
SELECT UNIQUE STANDARD_HASH(owner || 'SCHEMA') AS "id:ID", owner AS "name", 'SCHEMA' AS ":LABEL"
FROM sys.dba_objects
UNION
SELECT UNIQUE STANDARD_HASH(owner || name || REPLACE(type, 'EVALUATION CONTXT', 'EVALUATION CONTEXT')) AS "id:ID", name AS "name", REPLACE(type, 'EVALUATION CONTXT', 'EVALUATION CONTEXT') AS ":LABEL"
FROM sys.dba_dependencies
UNION
SELECT UNIQUE STANDARD_HASH(referenced_owner || referenced_name || REPLACE(referenced_type, 'EVALUATION CONTXT', 'EVALUATION CONTEXT')) AS "id:ID", referenced_name AS "name", REPLACE(referenced_type, 'EVALUATION CONTXT', 'EVALUATION CONTEXT') AS ":LABEL"
FROM sys.dba_dependencies
UNION
SELECT UNIQUE STANDARD_HASH(owner || 'SCHEMA') AS "id:ID", owner AS "name", 'SCHEMA' AS ":LABEL"
FROM sys.dba_dependencies
UNION
SELECT UNIQUE STANDARD_HASH(referenced_owner || 'SCHEMA') AS "id:ID", referenced_owner AS "name", 'SCHEMA' AS ":LABEL"
FROM sys.dba_dependencies
;
