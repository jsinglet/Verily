package core.checkers;

import core.VerilyChainableAction;
import core.VerilyChecker;
import core.VerilyEnv;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class ContractTransformationChecker extends VerilyChecker {

    protected static String checkerName = "Contract Transformation Checker";

    public ContractTransformationChecker(){super(checkerName);}

    @Override
    public VerilyChainableAction check(VerilyEnv env, VerilyChainableAction lastCheckerResult) {
        return null;
    }
}
