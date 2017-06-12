# silver - analyze and visualize relations and dependencies in Neo4j

A tool for loading relational/dependency information from relational database systems into Neo4j for analysis and visualization.

The name "silver" is a word formed from the two-three-letter short form "relvis", which stands for RELation VISualization. Silver essentially creates Neo4j import tool compatible CSV files for relations (edges) between entities (vertexes) which can be loaded by `silver` itself or by the `neo4j-import` tool. The resulting model gives you a clear view on how various database objects are interrelated and owned.

Silver currently knows about Oracle. The intention is to write other dataSource handlers that can do the same for PostgreSQL, MySQL and DB2.

## Getting silver

You can download a pre-built release [from the release page here](https://github.com/rubin55/silver/releases).

## Quick command-line overview
See here the help output of silver when run without any arguments:

    Usage: silver [-c | -e | -h | -l | -s | -v] [-d]
     -c,--check     Check configuration settings.
     -d,--debug     Turn on debug messages.
     -e,--extract   Extract data from rdbms into csv files.
     -h,--help      Show this usage text.
     -l,--load      Load csv files into neo4j.
     -s,--setup     Configure settings interactively.
     -v,--version   Show the version.

Essentially, a typical `silver` session would be to first run `--setup` which interactively sets up a configuration for you. The configuration contains connection parameters for the relational database from where silver extracts the model information, and for the Neo4j graph database to use for loading the model.

After setup, you first run `--extract` which creates a couple of CSV files after which you can `--load` the data into a Neo4j graph database (note that the CSV files are also compatible with Neo4j's `neo4j-import` tool).

You can peruse the queries in the `cypher-recipes.cql` file included in the repository. The comments tell you what the query does. You can execute these queries using the Neo4j web interface usually located at http://localhost:7474/ or via the `cypher-shell` utility.

Note: if you simply want to play with a (very) simple model, and see how the queries work, run the `--setup` procedure and provide "fake" data for the RDBMS connection data, but do provide "valid" data for the Neo4j part. After that you can copy:

* `example-nodes.csv` to `$HOME/.silver/oracle-nodes.csv`
* `example-relations.csv` to `$HOME/.silver/oracle-relations.csv`

Finally, simply run `--load` and the example data will be loaded into your graph. The queries in `cypher-recipes.cql` work with this dataset.
