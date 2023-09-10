package com.tt.common.model;

import lombok.var;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Tuple2
 *
 * @param <T1> T1
 * @param <T2> T2
 * @author Shuang Yu
 */
public class Tuple2<T1, T2> implements Iterable<Object>, Serializable {

    private static final long serialVersionUID = -3518082018884860684L;

    final T1 t1;
    final T2 t2;

    public Tuple2(T1 item1, T2 item2) {
        this.t1 = item1;
        this.t2 = item2;
    }

    /**
     * Type-safe way to get the first object of this Tuples.
     *
     * @return The first object
     */
    public T1 getT1() {
        return t1;
    }

    /**
     * Type-safe way to get the second object of this Tuples.
     *
     * @return The second object
     */
    public T2 getT2() {
        return t2;
    }

    /**
     * Get the object at the given index.
     *
     * @param index The index of the object to retrieve. Starts at 0.
     * @return The object or {@literal null} if out of bounds.
     */
    @Nullable
    public Object get(int index) {
        switch (index) {
            case 0:
                return t1;
            case 1:
                return t2;
            default:
                return null;
        }
    }

    /**
     * Turn this {@code Tuple} into a {@link List List&lt;Object&gt;}.
     * The list isn't tied to this Tuple but is a <strong>copy</strong> with limited
     * mutability ({@code add} and {@code remove} are not supported, but {@code set} is).
     *
     * @return A copy of the tuple as a new {@link List List&lt;Object&gt;}.
     */
    public List<Object> toList() {
        return Arrays.asList(toArray());
    }

    /**
     * Turn this {@code Tuple} into a plain {@code Object[]}.
     * The array isn't tied to this Tuple but is a <strong>copy</strong>.
     *
     * @return A copy of the tuple as a new {@link Object Object[]}.
     */
    public Object[] toArray() {
        return new Object[]{t1, t2};
    }

    /**
     * Return an <strong>immutable</strong> {@link Iterator Iterator&lt;Object&gt;} around
     * the content of this {@code Tuple}.
     *
     * @return An unmodifiable {@link Iterator} over the elements in this Tuple.
     * @implNote As an {@link Iterator} is always tied to its {@link Iterable} source by
     * definition, the iterator cannot be mutable without the iterable also being mutable.
     * Since Tuples are <strong>immutable</strong>, so is the {@link Iterator}
     * returned by this method.
     */
    @Override
    public Iterator<Object> iterator() {
        return Collections.unmodifiableList(toList()).iterator();
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        var tuple2 = (Tuple2<?, ?>) o;
        return t1.equals(tuple2.t1) && t2.equals(tuple2.t2);
    }

    @Override
    public int hashCode() {
        var result = size();
        result = 31 * result + t1.hashCode();
        result = 31 * result + t2.hashCode();
        return result;
    }

    /**
     * Return the number of elements in this {@literal Tuples}.
     *
     * @return The size of this {@literal Tuples}.
     */
    public int size() {
        return 2;
    }

    /**
     * A Tuple String representation is the comma separated list of values, enclosed
     * in square brackets.
     *
     * @return the Tuple String representation
     */
    @Override
    public final String toString() {
        return tupleStringRepresentation(toArray()).insert(0, '[').append(']').toString();
    }

    static StringBuilder tupleStringRepresentation(Object... values) {
        var sb = new StringBuilder();
        for (var i = 0; i < values.length; i++) {
            var t = values[i];
            if (i != 0) {
                sb.append(',');
            }
            if (t != null) {
                sb.append(t);
            }
        }
        return sb;
    }
}
