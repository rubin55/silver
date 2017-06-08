package org.rubin55.silver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.pool.OracleDataSource;

class Extractor {
    private static final Logger log = (Logger) LoggerFactory.getLogger(Extractor.class);
    private static Configuration cfg = Configuration.getInstance();

    public static void extract() {

        try {
            log.debug("JDBC connection string is: " + cfg.getJdbcConnectionString());
            OracleDataSource ods = new OracleDataSource();
            ods.setURL(cfg.getJdbcConnectionString());
            ods.setUser(cfg.getJdbcUser());
            ods.setPassword(cfg.getJdbcPass());
            Connection conn = ods.getConnection();

            // Create Oracle DatabaseMetaData object
            DatabaseMetaData meta = conn.getMetaData();

            // gets driver info:
            log.debug("JDBC driver version: " + meta.getDriverVersion());
            log.info("Connected to: " + meta.getDatabaseProductVersion());

            execute(conn, cfg.getJdbcDriver() + "-nodes.sql", "nodes.csv");
            execute(conn, cfg.getJdbcDriver() + "-relations.sql", "relations.csv");

            conn.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    private static void execute(Connection conn, String sqlScript, String csvFile) {
        log.info("Executing queries from " + sqlScript + ", writing to " + cfg.getConfigurationPath() + "/" + csvFile);
        List<String> queryList = SQLHelper.createQueriesFromFile(Configuration.openFromClassPath(sqlScript));
        queryList.stream().forEach(x -> {
            try {
                log.debug("Executing query: " + x);
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(x);
                String out = cfg.getConfigurationPath() + "/" + csvFile;

                CSVHelper.resultSetToCsv(rset, out);

                rset.close();
                stmt.close();
            } catch (FileNotFoundException | SQLException e) {
                log.error(e.getMessage());
            }
        });
    }
}
