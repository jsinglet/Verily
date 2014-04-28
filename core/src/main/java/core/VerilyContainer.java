package core;
/*
 * core.VerilyContainer.java
 *
 * The main container for Verily. Handles all the dispatching and decoding for Verily.
 *
 */

import content.TemplateFactory;
import core.filters.*;
import exceptions.InvalidFormalArgumentsException;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verily.lang.*;
import verily.lang.exceptions.MethodNotMappedException;
import verily.lang.exceptions.TableHomomorphismException;
import reification.*;
import utils.VerilyUtil;
import verily.lang.util.MRRTableSet;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import static core.VerilyFilter.VerilyFilterAction.*;
import static core.Constants.*;
import static core.ResponseUtils.*;

public class VerilyContainer implements Container {

    private static VerilyContainer verilyContainer;
    final Logger logger = LoggerFactory.getLogger(VerilyContainer.class);
    private Executor threadPool;
    private Thread classReloader;
    private VerilyModificationWatcher modificationWatcher;
    private VerilyEnv env;

    List<VerilyFilter> filters = new ArrayList<VerilyFilter>();

    private VerilyContainer(VerilyEnv env) {
        this.setEnv(env);
        logger.info("Constructed new Verily container @ {}", new Date());
    }

    public static VerilyContainer getContainer(int threads) throws IOException, TableHomomorphismException {

        final Logger logger = LoggerFactory.getLogger(VerilyContainer.class);

        if (verilyContainer == null) {

            /**
             * Construct our world
             */
            VerilyEnv env = new VerilyEnv();

            env.setNumberOfThreads(threads);

            // where are we?
            env.setHome(Paths.get(""));

            //verify that this is a Verily application
            //env.getHome().

            // set up the translation table
            Harness applicationHarness = new Harness(env.getHome());

            MRRTableSet translationTable = applicationHarness.extractTranslationTable();

            env.setTranslationTable(translationTable);

            verilyContainer = new VerilyContainer(env);

            verilyContainer.initFilters();

            logger.info("Created new thread pool with [{}] threads.", threads);
            verilyContainer.threadPool = Executors.newFixedThreadPool(threads);

        }
        return verilyContainer;
    }

    protected void initFilters(){
        filters.clear();

        // Note, the filters MUST be in this order for things
        // to function correctly.
        filters.add(new StaticContentFilter());       // deliver static content
        filters.add(new AjaxHarnessFilter());         // deliver ajax bootstrapping
        filters.add(new MRRFilter());                 // main MRR pattern
        filters.add(new ContractValidationFilter());  // runtime validation of parameters (semantically this happens BEFORE, but because of the
                                                      // way that it is implemented, the filter is placed after.)

    }

    public static VerilyContainer getContainer() {
        if (verilyContainer == null) {
          throw new RuntimeException("Container not initialized.");
        }
        return verilyContainer;
    }

    public void reloadTranslationTable() throws IOException, TableHomomorphismException {
        Harness applicationHarness = new Harness(env.getHome());

        MRRTableSet translationTable = applicationHarness.extractTranslationTable();

        env.setTranslationTable(translationTable);

    }

    @Override
    public void handle(final Request request, final Response response) {

        threadPool.execute(new Runnable(){
            @Override
            public void run() {
                doHandle(request, response);
            }
        });

    }


    public void doHandle(Request request, Response response){

        VerilyFilter.VerilyFilterAction lastAction = null;
        long ts1 = System.currentTimeMillis();

        // move through filter chain
        for(VerilyFilter filter : filters) {

            // by design, this catch should never be reached. if this warning is ever emitted
            // it is considered a design flaw and show be fixed in the offending filter.
            try {
                lastAction = filter.handleRequest(request, response, getEnv(), lastAction);
            }catch(Exception e){
                logger.warn("[{}] - \"{} {}\" {} - last filter failed with exception {}", new Date(), request.getMethod(), request.getPath().getPath(), lastAction.getStatusCode(), e.getMessage());
                break;
            }

            if(lastAction==STOP){
                break;
            }else if(lastAction==ERROR){
                dispatchError(request, response, lastAction.getReason(), lastAction.getStatusCode());
                break;
            } // otherwise status is CONTINUE or OK.
        }

        long ts2 = System.currentTimeMillis();
        logger.info("[{}] - \"{} {}\" {} ({} ms)", new Date(), request.getMethod(), request.getPath().getPath(), lastAction.getStatusCode(), ts2 - ts1);

    }

    private void dispatchError(Request request, Response response, Object reason, int statusCode){

        if(statusCode==404){
            send404(request, response, logger);
        }else if(statusCode==400){
            FilterError e = (FilterError)reason;
            send400(request, response, e.getContext(), e.getMethod(), e.getReason(), logger);
        }else if(statusCode==500){
            FilterError e = (FilterError)reason;
            send500(request, response, e.getContext(), e.getMethod(), e.getReason(), logger);
        }
    }

    public VerilyEnv getEnv() {
        return env;
    }

    public void setEnv(VerilyEnv env) {
        this.env = env;
    }

    public void stopService() {
        if (classReloader != null) {
            modificationWatcher.scheduleShutdown();
        }
    }

    public void startServices() {
        if (getEnv().isReload()) {
            logger.info("Starting class reloading service...");

            modificationWatcher = new VerilyModificationWatcher(Paths.get("").resolve("src").resolve("main"));

            classReloader = new Thread(modificationWatcher);

            classReloader.start();


            logger.info("Done");
        }
    }


}
