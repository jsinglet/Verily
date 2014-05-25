package core.checkers;

import core.*;
import utils.OpenJMLUtil;
import verily.lang.JMLTransformationError;
import verily.lang.util.MRRTableSet;

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

        logger.info("[jml] Transforming contracts for JML...");

        // read the source located in .verily/gen/src/ and transform it
        // to be JML-compatible.

        // TODO
        JMLTransformationHarness applicationHarness = new JMLTransformationHarness(env.getHome().resolve(".verily").resolve("gen"));

        try {
            applicationHarness.transformToJMLCompatibleSource(env.getTranslationTable().getMethodTable(), env.getTranslationTable().getRouterTable());
        } catch (IOException e) {
            return getResult(ERROR, e);
        } catch (JMLTransformationError jmlTransformationError) {
            return getResult(ERROR, jmlTransformationError);
        }


        return OK;
    }
}
