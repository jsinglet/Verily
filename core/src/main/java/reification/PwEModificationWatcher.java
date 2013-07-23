package reification;

import core.PwEContainer;
import exceptions.PwECompileFailedException;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwe.lang.exceptions.TableHomomorphismException;
import utils.PwEUtil;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class PwEModificationWatcher implements Runnable, FileListener {

    private boolean shouldShutdown = false;

    final Logger logger = LoggerFactory.getLogger(PwEModificationWatcher.class);

    private Path dir;

    public PwEModificationWatcher(Path dir) {
        this.dir = dir;
    }

    public void scheduleShutdown() {
        this.shouldShutdown = true;
    }

    @Override
    public void run() {

        try {
            FileSystemManager manager = VFS.getManager();
            FileObject file = manager.resolveFile(dir.toFile(), "java");

            DefaultFileMonitor monitor = new DefaultFileMonitor(this);
            monitor.setRecursive(true);

            monitor.setDelay(500);

            monitor.addFile(file);
            monitor.start();

        } catch (IOException e) {

        }
    }


    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {

        logger.info("Reloading application classes: {}", fileChangeEvent.getFile().toString());


        try {
            PwEUtil.reloadProject();

            if (fileChangeEvent.getFile().getParent().getName().getBaseName().equals("methods") || fileChangeEvent.getFile().getParent().getName().getBaseName().equals("controllers")) {
                PwEContainer.getContainer().reloadTranslationTable();
            }

        } catch (PwECompileFailedException e) {
            logger.info("Errors exist in your project: {}", e.getMessage());
        } catch (TableHomomorphismException e) {
            logger.info("Could not reload the MeVC mappings. Won't touch the old one.");
            logger.error("Error was: {}", e.getMessage());

        }

        logger.info("Reloading Complete.");


    }
}
