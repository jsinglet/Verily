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
public class ContractTransformationChecker extends VerilyChecker {

    protected static String checkerName = "Contract Transformation Checker";

    public ContractTransformationChecker(){super(checkerName);}

    @Override
    public VerilyChainableAction check(VerilyEnv env, VerilyChainableAction lastCheckerResult) {

        if(env.isEnableContracts()==false){
            return CONTINUE;
        }

        // read the source located in .verily/gen/src/ and transform it
        // to be JML-compatible.

        // TODO


        return OK;
    }
}
