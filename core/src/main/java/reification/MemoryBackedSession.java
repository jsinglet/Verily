package reification;

import verily.lang.ReadableValue;
import verily.lang.WritableValue;

import java.util.HashMap;
import java.util.Map;

public class MemoryBackedSession extends Session {

    private static final Map<Context, Env> env;

    static {
        env = new HashMap<Context, Env>();
    }

    private MemoryBackedSession(Context ctx) {
        super(ctx);
    }

    public static Session withContext(Context ctx) {
        return new MemoryBackedSession(ctx);
    }


    @Override
    public WritableValue getValue(Context ctx, String name) {

        if (env.containsKey(ctx)) {
            return env.get(ctx).get(name);
        }

        return null;
    }

    @Override
    public void setValue(Context ctx, String name, WritableValue value) {
        if (env.containsKey(ctx) == false) {
            env.put(ctx, new Env());
        }
        env.get(ctx).put(name, value);
    }

    @Override
    public void updateValue(String name, WritableValue value) {
        //noop
    }
}
