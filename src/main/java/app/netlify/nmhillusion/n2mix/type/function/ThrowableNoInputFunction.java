package app.netlify.nmhillusion.n2mix.type.function;

/**
 * date: 2021-10-12
 * <p>
 * created-by: nmhillusion
 */
public interface ThrowableNoInputFunction<R> {
    R apply() throws Exception;
}
