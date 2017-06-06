package org.rubin55.silver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import ch.qos.logback.classic.Level;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    private static Options options = new Options();

    public static void main(String[] args) {

        // Sane logging black magic.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // Mutually exclusive options.
        OptionGroup group = new OptionGroup();
        group.addOption(new Option("c", "check", false, "Check configuration settings."));
        group.addOption(new Option("e", "extract", false, "Extract data from rdbms into csv files."));
        group.addOption(new Option("h", "help", false, "Show this usage text."));
        group.addOption(new Option("l", "load", false, "Load csv files into neo4j."));
        group.addOption(new Option("s", "setup", false, "Configure settings interactively."));
        group.addOption(new Option("v", "version", false, "Show the version."));
        options.addOptionGroup(group);

        // Make debug a combinable option.
        options.addOption(new Option("d", "debug", false, "Turn on debug messages."));

        // Parse command line.
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);
            if (args.length == 0) {
                throw new ParseException("Please specify an option");
            }

            Stream<Option> stream = Arrays.stream(line.getOptions());
            stream.forEach(x -> {
                switch (x.getLongOpt()) {
                case "debug":
                    Application.debug();
                    break;
                case "check":
                    Configuration.init();
                    Configuration.check();
                    break;
                case "extract":
                    Configuration.init();
                    Extractor.extract();
                    break;
                case "help":
                    Application.help();
                    break;
                case "load":
                    Configuration.init();
                    Loader.load();
                    break;
                case "setup":
                    Configuration.setup();
                    break;
                case "version":
                    Application.version();
                    break;

                }

            });

        } catch (ParseException e) {
            log.error(e.getMessage());
            help();
        }
    }

    private static void debug() {
        log.info("Setting loglevel to debug...");
        root.setLevel(Level.DEBUG);
    }

    private static void help() {
        log.debug("Constructing help object...");
        HelpFormatter formatter = new HelpFormatter();
        formatter.setSyntaxPrefix("Usage: ");
        formatter.printHelp("silver", options, true);
    }

    private static void version() {
        String releaseProperties = "/release.properties";
        log.debug("Attempting to construct version from " + releaseProperties);
        Properties properties = new Properties();
        InputStream resourceStream = Application.class.getResourceAsStream(releaseProperties);
        if (resourceStream == null) {
            log.error("Sorry, unable to find " + releaseProperties);
        }
        try {
            properties.load(resourceStream);
            System.out.println(properties.getProperty("version") + "\n" + properties.getProperty("copyright") + "\n" + properties.getProperty("license"));

        } catch (IOException e) {
            log.error("Could not load " + releaseProperties);
        }
    }
}
