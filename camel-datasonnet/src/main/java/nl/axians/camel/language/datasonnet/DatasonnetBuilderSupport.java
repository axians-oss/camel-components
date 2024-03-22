package nl.axians.camel.language.datasonnet;

import com.datasonnet.document.MediaType;
import org.apache.camel.support.builder.ValueBuilder;

import java.util.List;

public final class DatasonnetBuilderSupport {

    /**
     * Private constructor to prevent instantiation.
     */
    private DatasonnetBuilderSupport() {
    }

    public static ValueBuilder dsonnet(final String theExpression,
                                       final String... theInputNames) {
        return dsonnet(theExpression, null, theInputNames);
    }

    public static ValueBuilder dsonnet(final String theExpression,
                                       final Class<?> theResultType,
                                       final String... theInputNames) {
        DatasonnetExpression exp = new DatasonnetExpression(theExpression);
        exp.setResultType(theResultType);
        exp.setInputNames(List.of(theInputNames));
        return new ValueBuilder(exp);
    }

    public static String dsonnetVarName(final String theName,
                                        final String theContentType) {
        return DatasonnetConstants.VARIABLE + DatasonnetConstants.VARIABLE_SEPARATOR
                + theContentType + DatasonnetConstants.VARIABLE_SEPARATOR + theName;
    }

    public static String dsonnetVarName(final String theExpression,
                                        final MediaType theMediaType) {
        return dsonnetVarName(theExpression, theMediaType.toString());
    }


}
