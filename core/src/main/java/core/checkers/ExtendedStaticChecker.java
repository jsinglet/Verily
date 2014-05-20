package core.checkers;

import core.VerilyChainableAction;
import core.VerilyChecker;
import core.VerilyEnv;
import utils.OpenJMLUtil;

import java.io.IOException;

import static core.VerilyChainableAction.*;


/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class ExtendedStaticChecker extends VerilyChecker {

    protected static String checkerName = "Extended Static Checker";

    public ExtendedStaticChecker(){super(checkerName);}

    @Override
    public VerilyChainableAction check(VerilyEnv env, VerilyChainableAction lastCheckerResult) {

        // setup
        try {
            OpenJMLUtil.preJML();
        } catch (IOException e) {
            return getResult(ERROR, e);
        }

        // at this point, the contracts should be transformed.
        // run the extended static checker.

        return OK;
    }
}
