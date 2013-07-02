/*
 * PwEMain.java
 *
 * Main entrypoint into PwE.
 *
 */

import exceptions.PwECompileFailedException;
import org.apache.commons.cli.*;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;
import pwe.lang.exceptions.TableHomomorphismException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.NoSuchFileException;

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

        System.setProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, "true");
        System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
        System.setProperty(SimpleLogger.LOG_FILE_KEY, "System.out");
        System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");

    }

    final Logger logger = LoggerFactory.getLogger(PwEMain.class);

    public static void main(String args[]) {


        CommandLineParser parser = new PosixParser();
        int EXIT = 0;

        try {
            CommandLine line = parser.parse(argList, args);

            PwEMain m = new PwEMain();

            m.bootstrap(line);

            // compile the project
            PwEUtil.compileProject();

            m.ready();

        } catch (ParseException e) {
            // we aren't really interested in moving forward if this fails
            System.err.println(PwEUtil.getMessage("MsgParsingFailed") + e.getMessage());
            EXIT = 1;

        } catch (NoSuchFileException e) {
            System.err.println(PwEUtil.getMessage("MsgInvalidDirectoryFormat"));
            EXIT = 1;
        } catch (IOException e) {
            System.err.println(PwEUtil.getMessage("MsgContainerInitFailed") + e.getMessage());
            EXIT = 1;
        } catch (NumberFormatException e) {
            System.err.println(PwEUtil.getMessage("MsgInvalidPort"));
            EXIT = 1;

        } catch (TableHomomorphismException e) {
            System.err.println(PwEUtil.getMessage("MsgTableHomomorphism"));
        } catch (InterruptedException e) {
            // this is a little bit of an unexpected exception so we are going to bail ungracefully
            e.printStackTrace();
            EXIT = 1;
        } catch (PwECompileFailedException e) {
            System.err.println(PwEUtil.getMessage("MsgCompileFailed"));
            EXIT = 1;
        }

        // only make an explicit call to exit if we have an abnormal exit condition
        if (EXIT != 0) {
            System.exit(EXIT);
        }
    }

    public void bootstrap(CommandLine cl) throws IOException, NumberFormatException, TableHomomorphismException {

        int port = PwE.DEFAULT_PORT;

        if (cl.getOptionValue(PwE.ARG_PORT) != null) {
            port = Integer.parseInt(cl.getOptionValue(PwE.ARG_PORT));
        }

        logger.info("Bootstrapping PwE on port {}...", port);


        Container container = PwEContainer.getContainer();
        Server server = new ContainerServer(container);
        Connection connection = new SocketConnection(server);
        SocketAddress address = new InetSocketAddress(port);

        connection.connect(address);

        PwEContainer.getContainer().getEnv().setPort(port);

        logger.info("Bootstrapping complete.");

    }

    public void ready() throws IOException, TableHomomorphismException {
        logger.info("Bootstrapping complete and PwE ready to serve requests at http://localhost:{}/", PwEContainer.getContainer().getEnv().getPort());
    }
}
