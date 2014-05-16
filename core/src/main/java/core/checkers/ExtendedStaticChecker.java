package core.checkers;

import core.VerilyChainableAction;
import core.VerilyChecker;
import core.VerilyEnv;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class ExtendedStaticChecker extends VerilyChecker {

    protected static String checkerName = "Extended Static Checker";

    public ExtendedStaticChecker(){super(checkerName);}

    @Override
    public VerilyChainableAction check(VerilyEnv env, VerilyChainableAction lastCheckerResult) {
        return null;
    }
}
