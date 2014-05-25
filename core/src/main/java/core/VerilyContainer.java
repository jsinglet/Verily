package core;
/*
 * core.VerilyContainer.java
 *
 * The main container for Verily. Handles all the dispatching and decoding for Verily.
 *
 */

import core.checkers.ContractTransformationChecker;
import core.checkers.ExtendedStaticChecker;
import core.checkers.MRRChecker;
import core.checkers.RuntimeContractsToValidationChecker;
import core.filters.*;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.VerilyUtil;
import verily.lang.exceptions.TableHomomorphismException;
import reification.*;
import verily.lang.util.MRRTableSet;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import static core.VerilyChainableAction.*;
import static core.ResponseUtils.*;

public class VerilyContainer implements Container {

    private static VerilyContainer verilyContainer;
    final Logger logger = LoggerFactory.getLogger(VerilyContainer.class);
    private Executor threadPool;
    private Thread classReloader;
    private VerilyModificationWatcher modificationWatcher;
    private VerilyEnv env;

    List<VerilyFilter> filters = new ArrayList<VerilyFilter>();
    List<VerilyChecker> checkers = new ArrayList<VerilyChecker>();


    private VerilyContainer(VerilyEnv env) {
        this.setEnv(env);
        logger.info("Constructed new Verily container @ {}", new Date());
    }

    public static VerilyContainer getContainer(int threads) throws Exception {

        final Logger logger = LoggerFactory.getLogger(VerilyContainer.class);

        if (verilyContainer == null) {

            /**
             * Construct our world
             */
            VerilyEnv env = new VerilyEnv();

            env.setNumberOfThreads(threads);

            // where are we?
            env.setHome(Paths.get(""));

            verilyContainer = new VerilyContainer(env);

            verilyContainer.initCheckers();
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

    protected void initCheckers(){
        checkers.add(new MRRChecker());
        checkers.add(new ContractTransformationChecker());
        checkers.add(new ExtendedStaticChecker());
        checkers.add(new RuntimeContractsToValidationChecker());
    }

    public static VerilyContainer getContainer() {
        if (verilyContainer == null) {
          throw new RuntimeException("Container not initialized.");
        }
        return verilyContainer;
    }


    public void verilize() throws Exception {

        VerilyChainableAction lastResult = null;

        for(VerilyChecker checker : checkers){
            lastResult = checker.check(getEnv(), lastResult);

            if(lastResult==ERROR){
                Exception e = (Exception)lastResult.getReason();
                throw e;
            }

        }

        // always recompile after a verilize pass since we could have
        // actually modified source code
        logger.info("[verily] Reloading project...");
        VerilyUtil.reloadProjectFromGen();

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

        VerilyChainableAction lastAction = null;
        long ts1 = System.currentTimeMillis();

        int filterNumber = 1;
        // move through filter chain
        for(VerilyFilter filter : filters) {

            // by design, this catch should never be reached. if this warning is ever emitted
            // it is considered a design flaw and show be fixed in the offending filter.
            try {
                logger.info("[{}] - Executing Filter: {}", filterNumber, filter.getFilterName());
                lastAction = filter.handleRequest(request, response, getEnv(), lastAction);
                logger.info("\tFilter Status: {}", lastAction);
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

            filterNumber++;
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
