# silver - analyze and visualize relations and dependencies in Neo4j

A tool for loading relational/dependency information from relational database systems into Neo4j for analysis and visualization.

The name "silver" is a word formed from the two-three-letter short form "relvis", which stands for RELation VISualization. Silver essentially creates Neo4j specific CSV files for relations (edges) between entities (vertexes) which can be either off-line batch-loaded or Cypher LOAD CSV'ed into Neo4j. The resulting model gives you a clear view on how various database objects are inter-related and owned.

Silver currently knows about Oracle. The intention is to write other dataSource handlers that can do the same for PostgreSQL, MySQL and DB2.

Quick command-line overview:

    Usage: silver <option>
    where possible options are:
        -setup (interactive, neo4j and rdbms settings, writes config file)
        -check (check settings in config file, connectivity, existence and format of csv files)
        -extract (from db to csv using jdbc, csv)
        -load (csv into neo4j using cypher load csv)
        -help (show commands and parameters)
        -version (show version)
