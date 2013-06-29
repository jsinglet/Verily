/*
 * PwEMain.java
 *
 * Main entrypoint into PwE.
 *
 */

import org.apache.commons.cli.*;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class PwEMain {

    public static final Options argList = new Options();

    static {

        Option port = OptionBuilder.withArgName(PwE.ARG_PORTNUMBER)
                .hasArg()
                .withDescription("port number to bind to (default 8000)")
                .create(PwE.ARG_PORT);
        Option help = new Option(PwE.ARG_HELP, "display this help");

        argList.addOption(port);
        argList.addOption(help);

    }

    final Logger logger = LoggerFactory.getLogger(PwEMain.class);

    public static void main(String args[]) {


        CommandLineParser parser = new PosixParser();
        int EXIT = 0;

        try {
            CommandLine line = parser.parse(argList, args);
            new PwEMain().bootstrap(line);

        } catch (ParseException e) {
            // we aren't really interested in moving forward if this fails
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            EXIT = 1;
        } catch (IOException e) {
            System.err.println("Failed to initialize PwE Container. Reason: " + e.getMessage());
            EXIT = 1;
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number specification.");
            EXIT = 1;

        }

        // only make an explicit call to exit if we have an abnormal exit condition
        if (EXIT != 0) {
            System.exit(EXIT);
        }
    }

    public void bootstrap(CommandLine cl) throws IOException, NumberFormatException {

        int port = PwE.DEFAULT_PORT;

        if (cl.getOptionValue(PwE.ARG_PORT) != null) {
            port = Integer.parseInt(cl.getOptionValue(PwE.ARG_PORT));
        }

        logger.info("Bootstrapping PwE on port {}...", port);


        // handoff control of the process to the PwE container
        Container container = PwEContainer.getContainer();
        Server server = new ContainerServer(container);
        Connection connection = new SocketConnection(server);
        SocketAddress address = new InetSocketAddress(port);

        connection.connect(address);

        logger.info("Bootstrapping complete.");
    }


}
