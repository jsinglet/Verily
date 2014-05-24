package core.checkers;

import core.MRRHarness;
import core.VerilyChainableAction;
import core.VerilyChecker;
import core.VerilyEnv;
import verily.lang.exceptions.TableHomomorphismException;
import verily.lang.util.MRRTableSet;

import static core.VerilyChainableAction.*;

import java.io.IOException;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class MRRChecker extends VerilyChecker {

    protected static String checkerName = "MRR Checker";

    public MRRChecker(){super(checkerName);}

    @Override
    public VerilyChainableAction check(VerilyEnv env, VerilyChainableAction lastCheckerResult) {

        MRRHarness applicationHarness = new MRRHarness(env.getHome());

        MRRTableSet translationTable = null;
        try {
            translationTable = applicationHarness.extractTranslationTable();
        } catch (IOException e) {
            return getResult(ERROR, e);
        } catch (TableHomomorphismException e) {
            return getResult(ERROR, e);
        }

        env.setTranslationTable(translationTable);


        return OK;
    }

}
