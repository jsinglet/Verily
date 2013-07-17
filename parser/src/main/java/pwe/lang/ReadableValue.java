package pwe.lang;

import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;

public class ReadableValue<T extends Serializable> {
    protected /*@ spec_public @*/ T value;

    // ensures this.value != null && this.value == value;
    //@ assignable this.value;
    public ReadableValue(T value) {

        this.value = value;
    }

    //@ ensures \result == this.value;
    public /*@ pure @*/ T getValue() {
        return this.value;
    }
}
