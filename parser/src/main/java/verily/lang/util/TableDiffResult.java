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
    // note that the line number is a "best guess." Sometimes it's not possible to generate a
    // line number that makes sense.
    private Integer lineNumber;



    public TableDiffResult(TableDiffKind diffKind, String className, String methodName, VerilyParserModes.VerilyModeType existsIn, Integer lineNumber){
        this(diffKind, className, methodName, existsIn, lineNumber, false);
    }

    public TableDiffResult(TableDiffKind diffKind, String className, String methodName, VerilyParserModes.VerilyModeType existsIn, Integer lineNumber, boolean usedImplicitParams){
        this.diffKind   = diffKind;
        this.className  = className;
        this.methodName = methodName;
        this.existsIn   = existsIn;
        this.usedImplicitParams = usedImplicitParams;
        this.lineNumber = lineNumber;
    }


    private VerilyParserModes.VerilyModeType getExistsIn(){return existsIn;}
    private VerilyParserModes.VerilyModeType getDoesntExistIn(){
        if(getExistsIn()== VerilyParserModes.VerilyModeType.TYPE_ROUTER){
            return VerilyParserModes.VerilyModeType.TYPE_METHOD;
        }
        return VerilyParserModes.VerilyModeType.TYPE_ROUTER;
    }

    public Integer getLineNumber(){ return lineNumber;}
    public void setLineNumber(Integer lineNumber){this.lineNumber=lineNumber;}


    private String maybeLineNumber(){
        if(getLineNumber()!=null){
            return String.format("%s.java:%d ", className, lineNumber);
        }
        return "";
    }

    /**
     * Prints a nice text explanation for this particular diff...
     * @return
     */
    public String toString(){
        StringBuffer sb = new StringBuffer();

        if(diffKind==TableDiffKind.KIND_CLASS){
            sb.append(
                    String.format("%s[HomomorphismError] The %s \"%s\" exists in your %ss table, but not in your %ss table.", maybeLineNumber(), diffKind.toString(), className, getExistsIn(), getDoesntExistIn())
            );
        }else if(diffKind==TableDiffKind.KIND_METHOD){
            sb.append(
                    String.format("%s[ParityError] The %s \"%s.%s\" exists in your %ss but not in its matching %s", maybeLineNumber(), diffKind.toString(), className, methodName, getExistsIn(), getDoesntExistIn())
            );
        }else if(diffKind==TableDiffKind.KIND_SIGNATURE) {
            if(usedImplicitParams) {
                sb.append(
                        String.format("%s[SignatureMismatchError] The Signatures for the Method/Router pair %s.%s do not match. Note that the return type of the method is required to be a formal parameter of its Router.", maybeLineNumber(), className, methodName)
                );
            }else{
                sb.append(
                        String.format("%s[SignatureMismatchError] The Signatures for the Method/Router pair %s.%s do not match.", maybeLineNumber(), className, methodName)
                );
            }
        }else{
            throw new UnsupportedOperationException("Tried to perform a String conversion on an unknown diff type: " + diffKind.toString());
        }

        return sb.toString();
    }

}
