package org.rubin55.silver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    public static void check() {
        log.debug("Invoking check routine");
    }

    public static void setup() {
        log.debug("Invoking setup routine");

    }
}
