package app.netlify.nmhillusion.n2mix.type;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;

public class Pair<X, Y> extends AbstractMap.SimpleEntry<X, Y> implements Serializable {
    private X key;
    private Y value;

    public Pair(X key, Y value) {
        super(key, value);
        this.key = key;
        this.value = value;
    }

    public static <A, B> Map.Entry<A, B> of(A first, B second) {
        return new AbstractMap.SimpleEntry<>(first, second);
    }

    @Override
    public X getKey() {
        return key;
    }

    public X setKey(X key) {
        this.key = key;
        return key;
    }

    @Override
    public Y getValue() {
        return value;
    }

    @Override
    public Y setValue(Y value) {
        this.value = value;
        return value;
    }
}
