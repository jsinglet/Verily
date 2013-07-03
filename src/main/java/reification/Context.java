package reification;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.UUID;

public class Context {

    /**
     * The context (typically a cookie)
     */
    private String ctx;

    public Context(String ctx) {
        this.ctx = ctx;
    }

    public Context() {
        // generate a new session id
        this.ctx = UUID.randomUUID().toString();
    }

    public boolean equals(Object o) {
        if (o instanceof Context) {
            Context that = (Context) o;
            return this == that || this.ctx.equals(that.ctx);
        }
        return false;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(ctx).hashCode();
    }

    public String toString() {
        return ctx;
    }

}
