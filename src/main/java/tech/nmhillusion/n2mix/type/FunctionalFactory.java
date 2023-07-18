package tech.nmhillusion.n2mix.type;

import tech.nmhillusion.n2mix.helper.log.LogHelper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * date: 2021-10-15
 * <p>
 * created-by: nmhillusion
 */

public class FunctionalFactory {
    public static void main(String[] args) {
        final List<Integer> integers = Arrays.asList(6, 7, 8, 9, 1, 2);

        final List<Integer> evenIntegers =
                integers.stream()
                        .filter(predicateOf((Integer it) -> 0 == it % 2).and(it -> it >= 5))
                        .collect(Collectors.toList());

        LogHelper.getLogger(FunctionalFactory.class).info("even integers: " + evenIntegers);
    }

    public static <T> Predicate<T> predicateOf(Predicate<T> predicate) {
        return predicate;
    }

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }

    public static boolean not(boolean value) {
        return !value;
    }

    public static <T> Predicate<T> and(Predicate<T> predicate, Predicate<? super T> anotherPredicate) {
        return predicate.and(anotherPredicate);
    }

    public static <T> Predicate<T> or(Predicate<T> predicate, Predicate<? super T> anotherPredicate) {
        return predicate.or(anotherPredicate);
    }
}
