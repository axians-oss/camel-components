package nl.axians.camel.language.datasonnet;

import com.datasonnet.spi.Library;
import org.apache.camel.support.builder.ValueBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DatasonnetBuilderSupport {

    /**
     * Private constructor to prevent instantiation.
     */
    private DatasonnetBuilderSupport() {
    }

    /**
     * Creates a new DataSonnet {@link ValueBuilder}.
     *
     * @param theExpression The DataSonnet expression.
     * @param theInputs The DataSonnet inputs.
     * @return The DataSonnet {@link ValueBuilder}.
     */
    public static ValueBuilder dsonnet(
            @NotNull final String theExpression,
            final DataSonnetInput... theInputs) {
        return dsonnet(theExpression, null, theInputs);
    }

    /**
     * Creates a new DataSonnet {@link ValueBuilder}.
     *
     * @param theExpression The DataSonnet expression.
     * @param theResultType The result type.
     * @param theInputs The DataSonnet inputs.
     * @return The DataSonnet {@link ValueBuilder}.
     */
    public static ValueBuilder dsonnet(
            @NotNull final String theExpression,
            final Class<?> theResultType,
            final DataSonnetInput... theInputs) {
        DatasonnetExpression exp = new DatasonnetExpression(theExpression);
        exp.setResultType(theResultType);
        exp.setInputs(List.of(theInputs));
        return new ValueBuilder(exp);
    }

    /**
     * Creates a new DataSonnet {@link ValueBuilder}.
     *
     * @param theExpression The DataSonnet expression.
     * @param theLibraries The DataSonnet libraries to apply.
     * @param theResultType The result type.
     * @param theInputs The DataSonnet inputs.
     * @return The DataSonnet {@link ValueBuilder}.
     */
    public static ValueBuilder dsonnet(
            @NotNull final List<Library> theLibraries,
            @NotNull final String theExpression,
            final Class<?> theResultType,
            final DataSonnetInput... theInputs) {
        DatasonnetExpression exp = new DatasonnetExpression(theExpression);
        exp.setResultType(theResultType);
        exp.setInputs(List.of(theInputs));
        exp.setLibraries(theLibraries);
        return new ValueBuilder(exp);
    }

}
