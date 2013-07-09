package reification;

import pwe.lang.ReadableValue;

public abstract class Session {

    private Context ctx;

    protected Session(Context ctx) {
        this.ctx = ctx;
    }

    protected abstract ReadableValue getValue(Context ctx, String name);

    protected abstract void setValue(Context ctx, String name, ReadableValue value);


    public abstract void updateValue(String name, ReadableValue value);


    public ReadableValue getValue(String name) {
        return getValue(ctx, name);

    }

    public void setValue(String name, ReadableValue value) {
        setValue(ctx, name, value);
    }

}
