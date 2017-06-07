-- nodes
-- name:ID, :LABEL
SELECT UNIQUE name AS "name:ID", type AS ":LABEL"
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE referenced_name AS "name:ID", referenced_type AS ":LABEL"
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE owner AS "name:ID", 'SCHEMA' AS ":LABEL"
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE referenced_owner AS "name:ID", 'SCHEMA' AS ":LABEL"
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
;
