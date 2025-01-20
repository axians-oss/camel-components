package nl.axians.camel.language.datasonnet.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Input {

    String value();
    String mediaType() default "application/json";

}
