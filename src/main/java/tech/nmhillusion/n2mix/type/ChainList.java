package tech.nmhillusion.n2mix.type;

import java.util.ArrayList;
import java.util.Collection;

/**
 * date: 2021-10-29
 * <p>
 * created-by: nmhillusion
 */

public class ChainList<T> extends ArrayList<T> {
    public ChainList<T> chainAdd(T item) {
        super.add(item);
        return this;
    }

    public ChainList<T> chainAdd(int index, T item) {
        super.add(index, item);
        return this;
    }

    public ChainList<T> chainAddAll(Collection<? extends T> items) {
        super.addAll(items);
        return this;
    }

    public ChainList<T> chainAddAll(int index, Collection<? extends T> items) {
        super.addAll(index, items);
        return this;
    }
}
