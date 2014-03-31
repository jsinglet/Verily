package verily.lang;

import org.apache.commons.lang.StringUtils;
import verily.lang.exceptions.MethodNotMappedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VerilyTable {

    private int size;
    //
    // The method table: A chained hashmap of methods
    //
    private Map<String, Map<String, VerilyMethod>> mTable = new HashMap<String, Map<String, VerilyMethod>>();

    /**
     * Maps a method into the method table. Note that as an optimization we do not compute size() on the fly but
     * increment it here. If methods are removed ever the implementation will have to be modified.
     *
     * @param context
     * @param method
     */
    public void mapMethod(String context, VerilyMethod method) {

        if (mTable.get(context) == null) {
            mTable.put(context, new HashMap<String, VerilyMethod>());
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
    public VerilyMethod methodAt(String context, String method) throws MethodNotMappedException {

        Map<String, VerilyMethod> segment = mTable.get(context);

        if (segment != null) {

            VerilyMethod mappedMethod = segment.get(method);

            if (mappedMethod != null) {
                return mappedMethod;
            }
        }

        throw new MethodNotMappedException(String.format("Method %s in Context %s appears unmapped", method, context));
    }

    public int size() {
        return size;
    }

    public boolean hasMultipleMutations(){

        // first, build a set of all mutations. key -> mutation count
        Map<String,Integer> mutations = new HashMap<String,Integer>();

        for(String context : mTable.keySet()){

            for(String method : mTable.get(context).keySet()){

                List<VerilyType> params = mTable.get(context).get(method).getFormalParameters();

                for(VerilyType t : params){
                    if(t.isSessionWritable()){
                        if(mutations.containsKey(t.getName())){
                            mutations.put(t.getName(), mutations.get(t.getName())+1);
                        }else{
                            mutations.put(t.getName(), 1);
                        }
                    }
                }

            }
        }

        // check the table
        for(String key : mutations.keySet()){
            if(mutations.get(key).intValue() > 1){
                return true;
            }
        }

        return false;
    }

    /**
     * Checks that two tables satisfy the MeVC Contract.
     *
     * Some Rules:
     * 1) Methods and routers must exist in parity, even if a given method does nothing.
     * 2) The signatures of the functions must match, except for the case in which a method returns a value.
     * 3) In this case we check that the LAST argument of the Router matches the return value of it's matched method.
     *
     * @param controllerTable
     * @param methodTable
     * @return
     */
    public static boolean fulfillsMeVCContractWith(VerilyTable controllerTable, VerilyTable methodTable) {

        boolean sameContents = false;

        // simple size check first -- we don't care if they don't have at least the same elements
        if (controllerTable.size() != methodTable.size()) {
            return false;
        }

        //next, make sure all elements in THIS table are in THAT table   -- comparison should happen this(controller) with that(methods)
        compareTwo:
        for (String context : controllerTable.mTable.keySet()) {

	    // assume this will fail
	    sameContents = false;

            Map<String, VerilyMethod> thisSegment = controllerTable.mTable.get(context);
            Map<String, VerilyMethod> thatSegment = methodTable.mTable.get(context);

            // 1) Check that it has a matching method
            if (thatSegment == null) break;

            // find each method
            for (String method : thisSegment.keySet()) {

                VerilyMethod thisMethod = thisSegment.get(method);
                VerilyMethod thatMethod = thatSegment.get(method);

                // 2) check method exists
		if(thatMethod==null) break compareTwo;

		// 3) Check signatures match
		//
		// note that we might modify the table, so we make sure to copy the structure
		//
		List<VerilyType> thisMethodType = new ArrayList(thisMethod.getFormalParameters());
		List<VerilyType> thatMethodType = new ArrayList(thatMethod.getFormalParameters());
		JavaParser.TypeContext methodSignature = thatMethod.getType();

		if (methodSignature != null) {
		    // now it needs to be such that the controller should have one MORE parameter
		    if(thisMethodType.size()-1==thatMethodType.size()){
			// now the sizes will match
			thatMethodType.add(new VerilyType(methodSignature.getText(), thisMethodType.get(thisMethodType.size()-1).getName()));
		    }
		}

		if(thisMethodType.size()!=thatMethodType.size()) break compareTwo;

		for (int i = 0; i < thisMethodType.size(); i++) {

		    VerilyType t1 = thisMethodType.get(i);
		    VerilyType t2 = thatMethodType.get(i);

		    // total equality
		    if (t1.equals(t2)) {
			continue;
		    }

		    // equality by subclassing
		    if (t2.isSubClassOf(t1) && t1.getName().equals(t2.getName())) {
			continue;
		    }

		    break compareTwo;
		}
	    }
	    // assume this is the last time through
	    sameContents = true;
	}

        return sameContents;
    }


    // this is awful. just don't look at me.
    public String asASCIITable(){

        /**
         * Prints a table like so:
         *
         * | Endpoint              | Method Spec             | Verbs
         * +-----------------------+-------------------------+------
         * | /TestBasic/someMethod | (String arg1)           | [POST, GET]
         * | /TestBasic/someMethod | (String arg1, Integer a)|
         * | /TestBasic/someMethod | (String arg1)           |
         * | /TestBasic/someMethod | (String arg1)           |
         *
         *
         */

        String[] header = {"ENDPOINT","METHOD SPEC", "VERBS"};

        List<String> endpointColumn = new ArrayList<String>();
        List<String> methodSpecColumn = new ArrayList<String>();

        int endpointColumnWidth = 0;
        int methodSpecColumnWidth = 0;
        int verbColumnWidth = 10;
        int margin = 5;



        // fill up all the columns!
        for(String rootClass : mTable.keySet()){

            for(String methodName : mTable.get(rootClass).keySet()){

                String s1 = String.format("/%s/%s", rootClass, methodName);
                String s2 = "(" + StringUtils.join(mTable.get(rootClass).get(methodName).getFormalParameters(), ", ") + ")";

                endpointColumn.add(s1);
                methodSpecColumn.add(s2);

                if(s1.length() > endpointColumnWidth){
                    endpointColumnWidth = s1.length();
                }

                if(s2.length() > methodSpecColumnWidth){
                    methodSpecColumnWidth = s2.length();
                }
            }
        }

        StringBuffer buffer = new StringBuffer();

        // row seperator
        buffer.append(String.format("+-%s-+-%s-+-%s-+\n", StringUtils.rightPad("", endpointColumnWidth+margin, '-'), StringUtils.rightPad("", methodSpecColumnWidth+margin, '-'), StringUtils.rightPad("", verbColumnWidth+margin, '-')));

        // header
        buffer.append(String.format("| %s | %s | %s |\n", StringUtils.rightPad(header[0], endpointColumnWidth+margin), StringUtils.rightPad(header[1], methodSpecColumnWidth+margin), StringUtils.rightPad(header[2], verbColumnWidth+margin)));
        // row seperator
        buffer.append(String.format("+-%s-+-%s-+-%s-+\n", StringUtils.rightPad("", endpointColumnWidth+margin, '-'), StringUtils.rightPad("", methodSpecColumnWidth+margin, '-'), StringUtils.rightPad("", verbColumnWidth+margin, '-')));

        for(int i=0; i<endpointColumn.size(); i++) {

            String s1 = endpointColumn.get(i);
            String s2 = methodSpecColumn.get(i);


            // header
            buffer.append(String.format("| %s | %s | %s |\n", StringUtils.rightPad(s1, endpointColumnWidth+margin), StringUtils.rightPad(s2, methodSpecColumnWidth+margin), StringUtils.rightPad("[POST, GET]", verbColumnWidth+margin)));
            // row seperator
            buffer.append(String.format("+-%s-+-%s-+-%s-+\n", StringUtils.rightPad("", endpointColumnWidth+margin, '-'), StringUtils.rightPad("", methodSpecColumnWidth+margin, '-'), StringUtils.rightPad("", verbColumnWidth+margin, '-')));


        }


        return buffer.toString();



    }

    public Map<String, Map<String, VerilyMethod>> getTable(){
        return mTable;
    }

}
