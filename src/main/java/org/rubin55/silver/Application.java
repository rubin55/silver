package org.rubin55.silver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
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

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        // Sane logging black magic.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // Build options object.
        Options options = new Options();
        options.addOption(new Option("d", "debug", false, "Turn on debug messages."));

        OptionGroup group = new OptionGroup();
        group.addOption(new Option("c", "check", false, "Check configuration settings."));
        group.addOption(new Option("e", "extract", false, "Extract data from rdbms into csv files."));
        group.addOption(new Option("h", "help", false, "Show this usage text."));
        group.addOption(new Option("l", "load", false, "Load csv files into neo4j."));
        group.addOption(new Option("s", "setup", false, "Configure settings interactively."));
        group.addOption(new Option("v", "version", false, "Show the version."));
        options.addOptionGroup(group);

        HelpFormatter formatter = new HelpFormatter();

        // Parse command line.
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);
            if (args.length == 0) {
                throw new ParseException("Please specify an option");
            }

            Stream<Option> stream = Arrays.stream(line.getOptions());
            stream.forEach(x -> {

            });

        } catch (ParseException e) {
            formatter.setSyntaxPrefix("Usage: ");
            formatter.printHelp("silver", options, true);
        }
    }

    private static void version() {
        String releaseProperties = "/release.properties";
        Properties properties = new Properties();
        InputStream resourceStream = Application.class.getResourceAsStream(releaseProperties);
        if(resourceStream == null){
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
