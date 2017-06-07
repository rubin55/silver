# silver - analyze and visualize relations and dependencies in Neo4j

A tool for loading relational/dependency information from relational database systems into Neo4j for analysis and visualization.

The name "silver" is a word formed from the two-three-letter short form "relvis", which stands for RELation VISualization. Silver essentially creates Neo4j specific CSV files for relations (edges) between entities (vertexes) which can be either off-line batch-loaded or Cypher LOAD CSV'ed into Neo4j. The resulting model gives you a clear view on how various database objects are inter-related and owned.

Silver currently knows about Oracle. The intention is to write other dataSource handlers that can do the same for PostgreSQL, MySQL and DB2.

Quick command-line overview:

    Usage: silver [-c | -e | -h | -l | -s | -v] [-d]
     -c,--check     Check configuration settings.
     -d,--debug     Turn on debug messages.
     -e,--extract   Extract data from rdbms into csv files.
     -h,--help      Show this usage text.
     -l,--load      Load csv files into neo4j.
     -s,--setup     Configure settings interactively.
     -v,--version   Show the version.
