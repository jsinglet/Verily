package pwe.lang;

public class WritableValue<T> extends ReadableValue<T> {

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
