package com.tt.common.model;

import lombok.var;
import org.springframework.lang.Nullable;


/**
 * Tuple4
 *
 * @param <T1> T1
 * @param <T2> T2
 * @param <T3> T3
 * @param <T4> T4
 * @author Shuang Yu
 */
public class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {

    private static final long serialVersionUID = -4898704078143033129L;

    final T4 t4;

    public Tuple4(T1 t1, T2 t2, T3 t3, T4 t4) {
        super(t1, t2, t3);
        this.t4 = t4;
    }

    /**
     * Type-safe way to get the fourth object of this Tuples.
     *
     * @return The fourth object
     */
    public T4 getT4() {
        return t4;
    }

    @Nullable
    @Override
    public Object get(int index) {
        switch (index) {
            case 0:
                return t1;
            case 1:
                return t2;
            case 2:
                return t3;
            case 3:
                return t4;
            default:
                return null;
        }
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1, t2, t3, t4};
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple4)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        var tuple4 = (Tuple4) o;
        return t4.equals(tuple4.t4);
    }

    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + t4.hashCode();
        return result;
    }

    @Override
    public int size() {
        return 4;
    }
}
