package org.rubin55.silver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

class CSVHelper {

    private static final Logger log = (Logger) LoggerFactory.getLogger(CSVHelper.class);

    public static List<String[]> csvToList(String fileName) throws FileNotFoundException {
        List<String[]> list = new ArrayList<String[]>();
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        while ((line = br.readLine()) != null) {
                list.add(line.split(","));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return list;
    }
    public static void resultSetToCsv(ResultSet rset, String fileName) throws SQLException, FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(new File(fileName));
        ResultSetMetaData meta = rset.getMetaData();
        int numberOfColumns = meta.getColumnCount();
        String dataHeaders = meta.getColumnName(1);

        for (int i = 2; i < numberOfColumns + 1; i++) {
            dataHeaders += "," + meta.getColumnName(i);
        }

        printWriter.println(dataHeaders);

        while (rset.next()) {
            String row = rset.getString(1);

            for (int i = 2; i < numberOfColumns + 1; i++) {
                row += "," + rset.getString(i);
            }

            printWriter.println(row);
        }

        printWriter.close();
    }
}
