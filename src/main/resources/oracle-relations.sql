--relations
--:START_ID,:TYPE,:END_ID
SELECT UNIQUE STANDARD_HASH(owner || 'SCHEMA') AS ":START_ID", 'OWNS' AS ":TYPE", STANDARD_HASH(owner || object_name || object_type) AS ":END_ID"
FROM sys.dba_objects
UNION
SELECT UNIQUE STANDARD_HASH(owner || name || type) AS ":START_ID", 'REFERENCES' AS ":TYPE", STANDARD_HASH(referenced_owner || referenced_name || referenced_type) AS ":END_ID"
FROM sys.dba_dependencies
UNION
SELECT UNIQUE STANDARD_HASH(owner || 'SCHEMA') AS ":START_ID", 'OWNS' AS ":TYPE", STANDARD_HASH(owner || name || type) AS ":END_ID"
FROM sys.dba_dependencies
UNION
SELECT UNIQUE STANDARD_HASH(referenced_owner || 'SCHEMA') AS ":START_ID", 'OWNS' AS ":TYPE", STANDARD_HASH(referenced_owner || referenced_name || referenced_type) AS ":END_ID"
FROM sys.dba_dependencies
;
