package tech.nmhillusion.n2mix.type.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * date: 2022-08-04
 * <p>
 * created-by: nmhillusion
 */

public class MapFunctionFromList<T> {
    private final List<Function<T, T>> functionList = new ArrayList<>();

    public MapFunctionFromList<T> add(Function<T, T> func) {
        functionList.add(func);
        return this;
    }

    public Function<T, T> combine() {
        return (T input) -> {
            T result = functionList.get(0).apply(input);

            for (int funcIdx = 1; funcIdx < functionList.size(); ++funcIdx) {
                result = functionList.get(funcIdx).apply(result);
            }

            return result;
        };
    }
}
