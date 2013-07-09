package pwe.lang;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Internal type for mapping methods.
 */
public class PwEType {

    private String type;
    private String name;

    public PwEType(String type, String name) {
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
        if (o instanceof PwEType) {
            PwEType that = (PwEType) o;
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

}

