package verily.lang.util;

import verily.lang.VerilyParserModes;

public class TableDiffResult {

    public enum TableDiffKind {
        KIND_METHOD("Method"),
        KIND_CLASS("Class"),
        KIND_SIGNATURE("Signature");

        private String kind;

        TableDiffKind(String kind){
            this.kind = kind;
        }
        public String toString(){return kind;}
    }

    private TableDiffKind diffKind;
    private String className;
    private String methodName;
    private VerilyParserModes.VerilyModeType existsIn;
    private boolean usedImplicitParams;

    public TableDiffResult(TableDiffKind diffKind, String className, String methodName, VerilyParserModes.VerilyModeType existsIn){
        this(diffKind, className, methodName, existsIn, false);
    }

    public TableDiffResult(TableDiffKind diffKind, String className, String methodName, VerilyParserModes.VerilyModeType existsIn, boolean usedImplicitParams){
        this.diffKind   = diffKind;
        this.className  = className;
        this.methodName = methodName;
        this.existsIn   = existsIn;
        this.usedImplicitParams = usedImplicitParams;
    }


    private VerilyParserModes.VerilyModeType getExistsIn(){return existsIn;}
    private VerilyParserModes.VerilyModeType getDoesntExistIn(){
        if(getExistsIn()== VerilyParserModes.VerilyModeType.TYPE_ROUTER){
            return VerilyParserModes.VerilyModeType.TYPE_METHOD;
        }
        return VerilyParserModes.VerilyModeType.TYPE_ROUTER;
    }



    /**
     * Prints a nice text explanation for this particular diff...
     * @return
     */
    public String toString(){
        StringBuffer sb = new StringBuffer();

        if(diffKind==TableDiffKind.KIND_CLASS){
            sb.append(
                    String.format("[HomomorphismError] The %s \"%s\" exists in your %ss table, but not in your %ss table.", diffKind.toString(), className, getExistsIn(), getDoesntExistIn())
            );
        }else if(diffKind==TableDiffKind.KIND_METHOD){
            sb.append(
                    String.format("[ParityError] The %s \"%s.%s\" exists in your %ss but not in its matching %s", diffKind.toString(), className, methodName, getExistsIn(), getDoesntExistIn())
            );
        }else if(diffKind==TableDiffKind.KIND_SIGNATURE) {
            if(usedImplicitParams) {
                sb.append(
                        String.format("[SignatureMismatchError] The Signatures for the Method/Router pair %s.%s do not match. Note that the return type of the method is required to be a formal parameter of its Router.", className, methodName)
                );
            }else{
                sb.append(
                        String.format("[SignatureMismatchError] The Signatures for the Method/Router pair %s.%s do not match.", className, methodName)
                );
            }
        }else{
            throw new UnsupportedOperationException("Tried to perform a String conversion on an unknown diff type: " + diffKind.toString());
        }

        return sb.toString();
    }

}
