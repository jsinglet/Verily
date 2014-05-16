package core;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public abstract class VerilyChecker {

    protected final Logger logger = LoggerFactory.getLogger(VerilyChecker.class);
    protected String checkerName;

    public VerilyChecker(String filterName){
        this.checkerName = filterName;
    }
    public String getCheckerName(){
        return checkerName;
    }
    public abstract VerilyChainableAction check(VerilyEnv env, VerilyChainableAction lastCheckerResult);


    public static VerilyChainableAction getResult(VerilyChainableAction action, Object reason){
        action.setReason(reason);
        return action;
    }
}
