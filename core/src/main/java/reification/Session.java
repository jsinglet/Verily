package reification;

public abstract class Session {

    private Context ctx;

    protected Session(Context ctx) {
        this.ctx = ctx;
    }

    public abstract Object getValue(Context ctx, String name);

    public abstract void setValue(Context ctx, String name, Object value);

    public Object getValue(String name) {
        return getValue(ctx, name);

    }

    public void setValue(String name, Object value) {
        setValue(ctx, name, value);
    }

}
