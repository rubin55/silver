package org.rubin55.silver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.pool.OracleDataSource;

class Extractor {
    private static final Logger log = (Logger) LoggerFactory.getLogger(Extractor.class);
    private static Configuration cfg = Configuration.getInstance();

    public static void extract() {
        log.debug("Invoking extract routine");

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
            System.out.println("JDBC driver version: " + meta.getDriverVersion());
            System.out.println("Connected to: " + meta.getDatabaseProductVersion());

            Statement stmt = conn.createStatement();

            ResultSet rset = stmt.executeQuery("select 'Hello World' from dual");

            while (rset.next()) {
                System.out.println(rset.getString(1));
            }

            rset.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            log.error(e.getMessage());
        }
  }
}
