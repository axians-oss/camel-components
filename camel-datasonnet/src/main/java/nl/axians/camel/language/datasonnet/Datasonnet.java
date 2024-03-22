package nl.axians.camel.language.datasonnet;

import org.apache.camel.support.language.LanguageAnnotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@LanguageAnnotation(language = "datasonnet", factory = DatasonnetAnnotationExpressionFactory.class)
public @interface Datasonnet {

    /**
     * The datasonnet expression which will be applied
     */
    String value();

    /**
     * The desired return type from the evaluated datasonnet.
     */
    Class<?> resultType() default Object.class;

    /**
     * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of
     * source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the
     * message body.
     */
    String source() default "";

    /**
     * The message body media type.
     */
    String bodyMediaType() default "";

    /**
     * The media type to output.
     */
    String outputMediaType() default "";

    /**
     * The extra input names that should be available in the datasonnet expression.
     */
    String[] inputNames();

}