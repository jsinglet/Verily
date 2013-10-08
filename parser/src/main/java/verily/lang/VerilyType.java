package verily.lang;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Internal type for mapping methods.
 */
public class VerilyType {

    private String type;
    private String name;

    public VerilyType(String type, String name) {
        this.setType(type);
        this.setName(name);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (o instanceof VerilyType) {
            VerilyType that = (VerilyType) o;
            return this == that || (this.getType().equals(that.getType()) && this.getName().equals(that.getName()));
        }
        return false;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(type).append(name).hashCode();
    }

    public boolean isSessionBound() {
        return (getType().startsWith("ReadableValue<") || getType().startsWith("WritableValue<"));
    }

    public boolean isSessionWritable() {
        return getType().startsWith("WritableValue<");
    }

    public boolean isSessionReadable() {
        return getType().startsWith("ReadableValue<");
    }


    // check if this is a subtype of that.
    public boolean isSubClassOf(VerilyType that){
        //TODO - extend this to more types.

        String thisType = this.getType();
        String thatType = that.getType();


        if(thisType.equals(thatType)){
            return true;
        }

        if(thisType.startsWith("WritableValue<") && thatType.startsWith("ReadableValue<")){
            return true;
        }

        return false;
    }

    public String toString()
    {
        return String.format("%s %s", type, name);
    }
}

