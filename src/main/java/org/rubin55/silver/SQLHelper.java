package org.rubin55.silver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

public class SQLHelper {
    private static final Logger log = (Logger) LoggerFactory.getLogger(SQLHelper.class);

    private static List<String> listOfQueries = null;

    public static List<String> createQueriesFromFile(InputStream is) {
        String queryLine = new String();
        StringBuffer sBuffer = new StringBuffer();
        listOfQueries = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            //Read the SQL file line by line.
            while ((queryLine = br.readLine()) != null) {

                // Ignore comments beginning with #.
                int indexOfCommentSign = queryLine.indexOf('#');
                if (indexOfCommentSign != -1) {
                    if (queryLine.startsWith("#")) {
                        queryLine = new String("");
                    } else
                        queryLine = new String(queryLine.substring(0, indexOfCommentSign - 1));
                }

                // Ignore comments beginning with --.
                indexOfCommentSign = queryLine.indexOf("--");
                if (indexOfCommentSign != -1) {
                    if (queryLine.startsWith("--")) {
                        queryLine = new String("");
                    } else
                        queryLine = new String(queryLine.substring(0, indexOfCommentSign - 1));
                }

                // Ignore comments surrounded by /* */.
                indexOfCommentSign = queryLine.indexOf("/*");
                if (indexOfCommentSign != -1) {
                    if (queryLine.startsWith("#")) {
                        queryLine = new String("");
                    } else
                        queryLine = new String(queryLine.substring(0, indexOfCommentSign - 1));

                    sBuffer.append(queryLine + " ");

                    // Ignore all characters within the comment.
                    do {
                        queryLine = br.readLine();
                    } while (queryLine != null && !queryLine.contains("*/"));
                    indexOfCommentSign = queryLine.indexOf("*/");
                    if (indexOfCommentSign != -1) {
                        if (queryLine.endsWith("*/")) {
                            queryLine = new String("");
                        } else
                            queryLine = new String(queryLine.substring(indexOfCommentSign + 2, queryLine.length() - 1));
                    }
                }

                // The following is necessary because otherwise the content before and after a line break
                //  are concatenated, e.g. "a.xyz FROM" would become "a.xyzFROM".
                if (queryLine != null)
                    sBuffer.append(queryLine + " ");
            }
            br.close();

            // We use ";" as a delimiter for each query.
            String[] splittedQueries = sBuffer.toString().split(";");

            // Filter out empty statements.
            for (int i = 0; i < splittedQueries.length; i++) {
                if (!splittedQueries[i].trim().equals("") && !splittedQueries[i].trim().equals("\t")) {
                    listOfQueries.add(new String(splittedQueries[i]));
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return listOfQueries;
    }
}
