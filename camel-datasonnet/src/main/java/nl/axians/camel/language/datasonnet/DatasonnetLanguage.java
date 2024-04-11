package nl.axians.camel.language.datasonnet;

import com.datasonnet.Mapper;
import com.datasonnet.document.MediaType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.spi.annotations.Language;
import org.apache.camel.support.SingleInputTypedLanguageSupport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Language("datasonnet")
public class DatasonnetLanguage extends SingleInputTypedLanguageSupport {

    /**
     * Create map of Datasonnet libraries found on the classpath.
     */
    private static final Map<String, String> CLASSPATH_IMPORTS = new HashMap<>();
    static {
        log.debug("One time classpath search...");
        try (ScanResult scanResult = new ClassGraph().acceptPaths("/").scan()) {
            try {
                scanResult.getResourcesWithExtension("libsonnet")
                        .forEachByteArrayThrowingIOException((resource, bytes) -> {
                            log.debug("Loading DataSonnet library: {}", resource.getPath());
                            CLASSPATH_IMPORTS.put(resource.getPath(), new String(bytes, StandardCharsets.UTF_8));
                        });
            } catch (IOException ignored) {
            }
        }
        log.debug("One time classpath search done");
    }

    /**
     * Cache of compiled DataSonnet scripts.
     */
//    private final Map<String, Mapper> mapperCache = LRUCacheFactory.newLRUSoftCache(16, 1000, true);
    private final Map<String, Mapper> mapperCache = new HashMap<>(16);

    @Override
    public Predicate createPredicate(Expression source, String expression, Object[] properties) {
        return (Predicate) createExpression(source, expression, properties);
    }

    @Override
    public Predicate createPredicate(String expression) {
        return createPredicate(null, expression, null);
    }

    @Override
    public Predicate createPredicate(String expression, Object[] properties) {
        return createPredicate(null, expression, properties);
    }

    @Override
    public Expression createExpression(String expression) {
        return createExpression(null, expression, null);
    }

    @Override
    public Expression createExpression(String expression, Object[] properties) {
        return createExpression(null, expression, properties);
    }

    /**
     * Create a new {@link DatasonnetExpression}.
     *
     * @param theSource The source expression.
     * @param theExpression The DataSonnet expression.
     * @param theProperties The properties.
     * @return The new {@link DatasonnetExpression}.
     */
    @Override
    public Expression createExpression(final Expression theSource,
                                       final String theExpression,
                                       final Object[] theProperties) {
        // Load the resource if theExpression is a resource URI otherwise use the expression as is.
        final String expression = loadResource(theExpression);
        final String bodyMediaType = property(String.class, theProperties, 2, null);
        final String outputMediaType = property(String.class, theProperties, 3, null);

        final DatasonnetExpression expr = new DatasonnetExpression(expression);
        expr.setSource(theSource);
        expr.setResultType(property(Class.class, theProperties, 0, null));

        if (bodyMediaType != null) {
            expr.setBodyMediaType(MediaType.valueOf(bodyMediaType));
        }

        if (outputMediaType != null) {
            expr.setOutputMediaType(MediaType.valueOf(outputMediaType));
        }

        if (getCamelContext() != null) {
            expr.init(getCamelContext());
        }

        return expr;
    }

    /**
     * Lookup a compiled DataSonnet script.
     *
     * @param script The script to lookup.
     * @return The compiled DataSonnet script.
     */
    Optional<Mapper> lookup(final String script) {
        return Optional.ofNullable(mapperCache.get(script));
    }

    /**
     * Compute a DataSonnet script if it is not in the cache.
     *
     * @param theName The name of the script to compute.
     * @param mapperSupplier The supplier to compute the script if not present yet.
     */
    void computeIfMiss(final String theName,
                       final Supplier<Mapper> mapperSupplier) {
        mapperCache.computeIfAbsent(theName, k -> mapperSupplier.get());
    }

    /**
     * Get the map with imported Datasonnet scripts.
     *
     * @return The map with imported Datasonnet scripts.
     */
    public Map<String, String> getDatasonnetImports() {
        return CLASSPATH_IMPORTS;
    }

}
