package reification;

import core.VerilyContainer;
import exceptions.VerilyCompileFailedException;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verily.lang.exceptions.TableHomomorphismException;
import utils.VerilyUtil;
import verily.lang.util.TableDiffResult;

import java.io.IOException;
import java.nio.file.*;

public class VerilyModificationWatcher implements Runnable, FileListener {

    private boolean shouldShutdown = false;

    final Logger logger = LoggerFactory.getLogger(VerilyModificationWatcher.class);

    private Path dir;

    public VerilyModificationWatcher(Path dir) {
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
            VerilyUtil.reloadProject();

            if (fileChangeEvent.getFile().getParent().getName().getBaseName().equals("methods") || fileChangeEvent.getFile().getParent().getName().getBaseName().equals("routers")) {
                VerilyContainer.getContainer().verilize();
            }

        } catch (VerilyCompileFailedException e) {
            logger.info("Errors exist in your project: {}", e.getMessage());
        } catch (TableHomomorphismException e) {

            logger.error(e.getMessage());

            logger.error("MRR Contract Violations:");
            logger.error("========================");

            if(e.errorLocations!=null){
                int i=1;
                for(TableDiffResult r : e.errorLocations){
                    logger.error(String.format("%d: %s", i, r.toString()));
                    i++;
                }
            }
            logger.info("Could not reload the MRR mappings. Won't touch the old one.");
        }

        logger.info("Reloading Complete.");

    }
}
