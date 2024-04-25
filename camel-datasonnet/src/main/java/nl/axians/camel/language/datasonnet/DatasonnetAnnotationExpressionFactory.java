package nl.axians.camel.language.datasonnet;

import com.datasonnet.document.MediaType;
import org.apache.camel.CamelContext;
import org.apache.camel.Expression;
import org.apache.camel.support.builder.ExpressionBuilder;
import org.apache.camel.support.language.DefaultAnnotationExpressionFactory;
import org.apache.camel.support.language.LanguageAnnotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * The Datasonnet annotation expression factory.
 */
public class DatasonnetAnnotationExpressionFactory extends DefaultAnnotationExpressionFactory {

    /**
     * Create the expression from the {@link Datasonnet} annotation.
     *
     * @param theContext The Camel context.
     * @param theAnnotation The {@link Datasonnet} annotation.
     * @param theLanguageAnnotation The language annotation.
     * @param theExpressionReturnType The expression return type.
     * @return The expression.
     */
    @Override
    public Expression createExpression(final CamelContext theContext,
                                       final Annotation theAnnotation,
                                       final LanguageAnnotation theLanguageAnnotation,
                                       final Class<?> theExpressionReturnType) {
        final String expression = getExpressionFromAnnotation(theAnnotation);
        final DatasonnetExpression expr = new DatasonnetExpression(expression);

        // Result type.
        Class<?> resultType = getResultType(theAnnotation);
        if (resultType.equals(Object.class)) {
            resultType = theExpressionReturnType;
        }

        // Source.
        final String source = getSource(theAnnotation);
        expr.setSource(ExpressionBuilder.singleInputExpression(source));

        // Media types.
        if (theAnnotation instanceof Datasonnet annotation) {
            if (!annotation.bodyMediaType().isEmpty()) {
                expr.setBodyMediaType(MediaType.valueOf(annotation.bodyMediaType()));
            }

            if (!annotation.outputMediaType().isEmpty()) {
                expr.setOutputMediaType(MediaType.valueOf(annotation.outputMediaType()));
            }

            if (annotation.inputNames() != null) {
                expr.setInputNames(List.of(annotation.inputNames()));
            }
        }

        return ExpressionBuilder.convertToExpression(expr, resultType);
    }

    /**
     * Get the expression result type from the theAnnotation.
     *
     * @param theAnnotation The theAnnotation.
     * @return The expression result type.
     */
    protected Class<?> getResultType(final Annotation theAnnotation) {
        if (theAnnotation instanceof Datasonnet annotation) {
            return annotation.resultType();
        }

        return null;
    }

    /**
     * Get the expression source from the theAnnotation.
     *
     * @param theAnnotation The theAnnotation.
     * @return The expression source.
     */
    protected String getSource(Annotation theAnnotation) {
        String source = null;

        if (theAnnotation instanceof Datasonnet annotation) {
            source = annotation.source();
        }

        return (source != null && !source.isBlank()) ? source : null;
    }
}
