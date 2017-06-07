--relations
--:START_ID,:TYPE,:END_ID
SELECT UNIQUE name AS ":START_ID", 'REFERENCES' AS ":TYPE", referenced_name AS ":END_ID"
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE owner AS ":START_ID", 'OWNS' AS ":TYPE", name AS ":END_ID"
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
UNION
SELECT UNIQUE referenced_owner AS ":START_ID", 'OWNS' AS ":TYPE", referenced_name AS ":END_ID"
FROM sys.dba_dependencies
WHERE owner='HR' OR referenced_owner='HR'
;
