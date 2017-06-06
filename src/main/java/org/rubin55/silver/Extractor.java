package org.rubin55.silver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.pool.OracleDataSource;

class Extractor {
    private static final Logger log = LoggerFactory.getLogger(Extractor.class);

    public static void extract() {
        log.debug("Invoking extract routine");

        try {
            OracleDataSource ods = new OracleDataSource();
            ods.setURL(Configuration.getJdbcConnectionString());
            Connection conn = ods.getConnection();

            // Create Oracle DatabaseMetaData object
            DatabaseMetaData meta = conn.getMetaData();

            // gets driver info:
            System.out.println("JDBC driver version: " + meta.getDriverVersion());
            System.out.println("Connected to: " + meta.getDatabaseProductVersion());
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
  }
}
