package pwe.lang;

public class ReadableValue<T> {
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
