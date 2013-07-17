package pwe.lang;

import java.io.Serializable;

public final class WritableValue<T extends Serializable> extends ReadableValue<T> {

    // ensures this.value != null && this.value == value;
    //@ assignable this.value;
    public WritableValue(T value) {
        super(value);
    }

    // ensures this.value != null && this.value == value;
    //@ assignable this.value;
    public void setValue(T value) {
        this.value = value;
    }
}
