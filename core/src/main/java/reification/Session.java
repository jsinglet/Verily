package reification;

import verily.lang.ReadableValue;
import verily.lang.WritableValue;

public abstract class Session {

    private Context ctx;

    protected Session(Context ctx) {
        this.ctx = ctx;
    }

    protected abstract WritableValue getValue(Context ctx, String name);

    protected abstract void setValue(Context ctx, String name, WritableValue value);


    public abstract void updateValue(String name, WritableValue value);


    public WritableValue getValue(String name) {
        return getValue(ctx, name);

    }

    public void setValue(String name, WritableValue value) {
        setValue(ctx, name, value);
    }

}
