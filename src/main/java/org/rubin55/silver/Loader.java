package org.rubin55.silver;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

class Loader {
    private static final Logger log = (Logger) LoggerFactory.getLogger(Loader.class);
    private static Configuration cfg = Configuration.getInstance();

    public static void load() {
        log.debug("Invoking load routine");
        log.debug("Neo4j connection string is: " + cfg.getNeo4jConnectionString());
    }
}
