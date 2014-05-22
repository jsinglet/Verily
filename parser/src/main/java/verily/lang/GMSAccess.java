package verily.lang;

import java.util.LinkedList;
import java.util.List;

/**
 * @author John L. Singleton <jsinglet@gmail.com>
 */
public class GMSAccess {

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public enum AccessType {N,R,W};
    private String module;
    private AccessType type;
    private String name;
    private int line;
    private String method;


    public GMSAccess(String module, AccessType type, String name, String method, int line){
        this.setModule(module);
        this.setType(type);
        this.setName(name);
        this.setLine(line);
        this.setMethod(method);
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public AccessType getType() {
        return type;
    }

    public void setType(AccessType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<GMSAccess> writes(List<GMSAccess> l){

        List<GMSAccess> w = new LinkedList<GMSAccess>();

        for(GMSAccess a : l){
            if(a.getType()== GMSAccess.AccessType.W){
                w.add(a);
            }
        }

        return w;

    }


    public static List<GMSAccess> sublistByName(String name, List<GMSAccess> source){

        List<GMSAccess> r = new LinkedList<GMSAccess>();

        for(GMSAccess a : source){
            if(a.getName().equals(name)){
                r.add(a);
            }
        }

        return r;
    }


    public static List<GMSAccess> reads(List<GMSAccess> l){

        List<GMSAccess> r = new LinkedList<GMSAccess>();

        for(GMSAccess a : l){
            if(a.getType()== GMSAccess.AccessType.R){
                r.add(a);
            }
        }

        return r;

    }


    public static List<String> modules(List<GMSAccess> l){

        List<String> m = new LinkedList<String>();

        for(GMSAccess a : l){
            if(m.contains(a.getModule())==false){
                m.add(a.getModule());
            }
        }

        return m;

    }

    public static List<String> vars(List<GMSAccess> l){

        List<String> m = new LinkedList<String>();

        for(GMSAccess a : l){
            if(m.contains(a.getName())==false){
                m.add(a.getName());
            }
        }

        return m;

    }

    public static List<String> varsInModule(List<GMSAccess> l, String module){

        List<String> m = new LinkedList<String>();

        for(GMSAccess a : l){
            if(m.contains(a.getName())==false  && a.getModule().equals(module)){
                m.add(a.getName());
            }
        }

        return m;

    }

    public static List<String> methodsInModule(List<GMSAccess> l, String module){

        List<String> m = new LinkedList<String>();

        for(GMSAccess a : l){
            if(m.contains(a.getMethod())==false  && a.getModule().equals(module)){
                m.add(a.getMethod());
            }
        }

        return m;

    }

}

