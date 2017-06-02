-- nodes
-- name:ID, :LABEL
SELECT UNIQUE name AS name, type AS label
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE referenced_name AS name, referenced_type AS label
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE owner AS name, 'SCHEMA' AS label
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE referenced_owner AS name, 'SCHEMA' AS label
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
;
--relations
--:START_ID,:TYPE,:END_ID
SELECT UNIQUE name AS source, 'REFERENCES' AS type, referenced_name AS target
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE owner AS source, 'OWNS' AS type, name AS target
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE referenced_owner AS source, 'OWNS' AS type, referenced_name AS target
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
;
