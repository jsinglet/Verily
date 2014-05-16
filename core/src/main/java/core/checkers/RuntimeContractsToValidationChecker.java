package core.checkers;

import core.VerilyChainableAction;
import core.VerilyChecker;
import core.VerilyEnv;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */

import static core.VerilyChainableAction.*;

public class RuntimeContractsToValidationChecker extends VerilyChecker {

    protected static String checkerName = "Contracts Checker";

    public RuntimeContractsToValidationChecker(){super(checkerName);}

    @Override
    public VerilyChainableAction check(VerilyEnv env, VerilyChainableAction lastCheckerResult) {

        if(env.isEnableContracts()==false){
            return CONTINUE;
        }

        //
        // run checker.
        //



        return OK;
    }
}
