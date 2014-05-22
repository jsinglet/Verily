package verily.lang.util;

import verily.lang.VerilyTable;

public class MRRTableSet {

    private VerilyTable methods;
    private VerilyTable routers;

    public MRRTableSet(VerilyTable methods, VerilyTable routers){
        this.methods = methods;
        this.routers = routers;
    }

    public VerilyTable getMethodTable(){
        return this.methods;
    }
    public VerilyTable getRouterTable(){
        return this.routers;
    }

}

