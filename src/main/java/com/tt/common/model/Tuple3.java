package com.tt.common.model;

import lombok.var;
import org.springframework.lang.Nullable;


/**
 * Tuple3
 *
 * @param <T1> T1
 * @param <T2> T2
 * @param <T3> T3
 * @author Shuang Yu
 */

public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {

    private static final long serialVersionUID = -4430274211524723033L;

    final T3 t3;

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        super(t1, t2);
        this.t3 = t3;
    }

    /**
     * Type-safe way to get the third object of this Tuples.
     *
     * @return The third object
     */
    public T3 getT3() {
        return t3;
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
            default:
                return null;
        }
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1, t2, t3};
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple3)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        var tuple3 = (Tuple3) o;
        return t3.equals(tuple3.t3);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + t3.hashCode();
        return result;
    }

    @Override
    public int size() {
        return 3;
    }
}
