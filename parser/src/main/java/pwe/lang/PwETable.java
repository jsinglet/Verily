package pwe.lang;

import pwe.lang.exceptions.MethodNotMappedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PwETable {

    private int size;
    //
    // The method table: A chained hashmap of methods
    //
    private Map<String, Map<String, PwEMethod>> mTable = new HashMap<String, Map<String, PwEMethod>>();

    /**
     * Maps a method into the method table. Note that as an optimization we do not compute size() on the fly but
     * increment it here. If methods are removed ever the implementation will have to be modified.
     *
     * @param context
     * @param method
     */
    public void mapMethod(String context, PwEMethod method) {

        if (mTable.get(context) == null) {
            mTable.put(context, new HashMap<String, PwEMethod>());
        }

        mTable.get(context).put(method.getMethod(), method);

        size++;
    }

    /**
     * Locates a mapped method
     *
     * @param context
     * @param method
     * @return
     * @throws MethodNotMappedException
     */
    public PwEMethod methodAt(String context, String method) throws MethodNotMappedException {

        Map<String, PwEMethod> segment = mTable.get(context);

        if (segment != null) {

            PwEMethod mappedMethod = segment.get(method);

            if (mappedMethod != null) {
                return mappedMethod;
            }
        }

        throw new MethodNotMappedException(String.format("Method %s in Context %s appears unmapped", method, context));
    }

    public int size() {
        return size;
    }

    public boolean equals(PwETable that) {

        boolean sameContents = true;
        boolean sameAddress = (this == that);

        // simple size check first -- we don't care if they don't have at least the same elements
        if (this.size() != that.size()) {
            return false;
        }

        //next, make sure all elements in THIS table are in THAT table
        compareTwo:
        for (String context : this.mTable.keySet()) {

            Map<String, PwEMethod> thisSegment = this.mTable.get(context);
            Map<String, PwEMethod> thatSegment = that.mTable.get(context);

            // we have the context
            if (thatSegment != null) {

                // find each method
                for (String method : thisSegment.keySet()) {

                    PwEMethod thisMethod = thisSegment.get(method);
                    PwEMethod thatMethod = thatSegment.get(method);

                    // method exists
                    if (thatMethod != null) {

                        // signatures match
                        List<PwEType> thisMethodType = thisMethod.getFormalParameters();
                        List<PwEType> thatMethodType = thatMethod.getFormalParameters();

                        if (thisMethodType.size() == thatMethodType.size()) {

                            for (int i = 0; i < thisMethodType.size(); i++) {

                                PwEType t1 = thisMethodType.get(i);
                                PwEType t2 = thatMethodType.get(i);

                                if (t1.equals(t2)) {
                                    continue;
                                }

                                sameContents = false;
                                break compareTwo;
                            }
                        } else {
                            sameContents = false;
                            break compareTwo;
                        }


                    } else {
                        sameContents = false;
                        break compareTwo;
                    }

                }
            } else {
                sameContents = false;
                break;
            }
        }

        return sameAddress || sameContents;
    }
}
