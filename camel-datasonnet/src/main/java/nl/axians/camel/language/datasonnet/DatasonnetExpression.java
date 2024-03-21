package nl.axians.camel.language.datasonnet;

import com.datasonnet.Mapper;
import com.datasonnet.MapperBuilder;
import com.datasonnet.document.DefaultDocument;
import com.datasonnet.document.Document;
import com.datasonnet.document.MediaType;
import com.datasonnet.document.MediaTypes;
import com.datasonnet.header.Header;
import com.datasonnet.spi.Library;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.spi.ExpressionResultTypeAware;
import org.apache.camel.support.ExchangeHelper;
import org.apache.camel.support.ExpressionAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static nl.axians.camel.language.datasonnet.DatasonnetConstants.VARIABLE;

/**
 * Represents a Datasonnet expression.
 */
@Slf4j
public class DatasonnetExpression extends ExpressionAdapter implements ExpressionResultTypeAware {

    private final String expression;

    @Getter
    @Setter
    private MediaType bodyMediaType;

    @Getter
    @Setter
    private MediaType outputMediaType;

    @Setter
    private Class<?> resultType;

    @Getter
    @Setter
    private Expression source;

    @Getter
    @Setter
    private Collection<String> libraryPaths;

    private DatasonnetLanguage language;

    /**
     * Create a new {@link DatasonnetExpression} with the given expression.
     *
     * @param theExpression The Datasonnet expression.
     */
    public DatasonnetExpression(final String theExpression) {
        expression = theExpression;
    }

    /**
     * Initialize the Datasonnet expression with the given {@link CamelContext}.
     *
     * @param theContext The {@link CamelContext} to use for initialization.
     */
    @Override
    public void init(final CamelContext theContext) {
        super.init(theContext);

        language = (DatasonnetLanguage) theContext.resolveLanguage("datasonnet");
        language.computeIfMiss(expression, () -> {
            MapperBuilder builder = new MapperBuilder(expression)
                    .withInputNames("body")
                    .withImports(resolveImports(language))
                    .withDefaultOutput(MediaTypes.APPLICATION_JAVA);

            Set<Library> additionalLibraries = theContext.getRegistry().findByType(Library.class);
            for (Library lib : additionalLibraries) {
                builder = builder.withLibrary(lib);
            }
            return builder.build();
        });
    }

    /**
     * Check if the Datasonnet expression matches. Used if the expression is used in a {@link Predicate}.
     *
     * @param theExchange The {@link Exchange} for which to evaluate the expression.
     * @return True if the expression matches, false otherwise.
     */
    @Override
    public boolean matches(final Exchange theExchange) {
        outputMediaType = MediaTypes.APPLICATION_JAVA;
        return evaluate(theExchange, Boolean.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(final Exchange theExchange,
                          final Class<T> theType) {
        Document<?> result = doEvaluate(theExchange);
        if (!theType.equals(Object.class)) {
            return ExchangeHelper.convertToType(theExchange, theType, result.getContent());
        } else if (resultType == null || resultType.equals(Document.class)) {
            return (T) result;
        } else {
            return (T) result.getContent();
        }
    }

    /**
     * Do evaluate the Datasonnet expression using the specified exchange.
     *
     * @param theExchange The exchange to use for evaluation.
     * @return The result of the evaluation as a {@link Document}.
     */
    private Document<?> doEvaluate(final Exchange theExchange) {
        final MediaType bodyMediaType = getBodyMediaType(theExchange);
        final MediaType outputMediaType = getOutputMediaType(theExchange);
        final Document<?> body = getBodyAsDocument(theExchange, bodyMediaType);
        final Map<String, Document<?>> inputs = getInputs(theExchange);

        final Mapper mapper = language.lookup(expression).orElseThrow(() ->
                new IllegalStateException("Datasonnet expression not initialized!"));
        if (resultType == null || resultType.equals(Document.class)) {
            return mapper.transform(body, inputs, outputMediaType, Object.class);
        } else {
            return mapper.transform(body, inputs, outputMediaType, resultType);
        }
    }

    /**
     * Get the payload from the exchange as a Datasonnet {@link Document}. If a source is set it will evaluate the
     * source expression using the provided {@link Exchange} otherwise it will get the body from that exchange.
     *
     * @param theExchange      The exchange from which to get the payload.
     * @param theBodyMediaType The {@link MediaType} of the body.
     * @return The payload representing the body of the exchange.
     */
    private Document<?> getBodyAsDocument(final Exchange theExchange,
                                          final MediaType theBodyMediaType) {
        final Object payload = (source != null) ? source.evaluate(theExchange, Object.class) : theExchange.getIn().getBody();
        return getDocument(theExchange, theBodyMediaType, payload);
    }

    /**
     * Get a {@link Document} from the given content. If the content is already a {@link Document} it will return
     * that.
     *
     * @param theExchange  The exchange for which to get the document.
     * @param theMediaType The {@link MediaType} of the content.
     * @param theContent   The content to convert to a {@link Document}.
     * @return The {@link Document} representing the content.
     */
    @NotNull
    private Document<?> getDocument(final Exchange theExchange,
                                    final MediaType theMediaType,
                                    final Object theContent) {
        Document<?> document = null;

        // Try to convert the payload to a Datasonnet Document using type conversion.
        if (theContent != null) {
            document = theExchange.getContext().getTypeConverter().tryConvertTo(Document.class, theExchange, theContent);
        }

        // If conversion failed, try to create a new Document from the payload as a String.
        if (document == null) {
            final String text = theExchange.getContext().getTypeConverter().tryConvertTo(String.class, theExchange, theContent);
            if (theExchange.getMessage().getBody() == null || "".equals(text)) {
                document = new DefaultDocument<>("", MediaTypes.APPLICATION_JAVA);
            } else if (MediaTypes.APPLICATION_JAVA.equalsTypeAndSubtype(theMediaType) || theMediaType == null) {
                document = new DefaultDocument<>(theContent);
            } else {
                document = new DefaultDocument<>(text, bodyMediaType);
            }
        }

        return document;
    }

    /**
     * Get the body {@link MediaType} from the exchange. If not set in the Datasonnet expression itself it will try to
     * get it from the exchange by first looking at the {@link DatasonnetConstants#BODY_MEDIATYPE} property and if that
     * is not set it will look at the {@link Exchange#CONTENT_TYPE} header.
     *
     * @param theExchange The exchange from which to get the body {@link MediaType}.
     * @return The body {@link MediaType}.
     */
    private MediaType getBodyMediaType(final Exchange theExchange) {
        MediaType mediaType = bodyMediaType;
        if (mediaType == null && !expression.startsWith(Header.DATASONNET_HEADER)) {
            final String contentType = theExchange.getProperty(DatasonnetConstants.BODY_MEDIATYPE,
                    theExchange.getIn().getHeader(Exchange.CONTENT_TYPE), String.class);
            if (contentType != null) {
                mediaType = MediaType.valueOf(contentType);
            }
        }

        return mediaType;
    }

    /**
     * Get the output {@link MediaType} from the exchange. If not set in the Datasonnet expression itself it will try
     * to get it from the exchange by first looking at the {@link DatasonnetConstants#OUTPUT_MEDIATYPE} property and if
     * that is not set it will look at the {@link DatasonnetConstants#OUTPUT_MEDIATYPE} header.
     *
     * @param theExchange The exchange from which to get the output {@link MediaType}.
     * @return The output {@link MediaType}.
     */
    private MediaType getOutputMediaType(final Exchange theExchange) {
        MediaType mediaType = outputMediaType;
        if (mediaType == null) {
            final String contentType = theExchange.getProperty(DatasonnetConstants.OUTPUT_MEDIATYPE,
                    theExchange.getIn().getHeader(DatasonnetConstants.OUTPUT_MEDIATYPE), String.class);
            if (contentType != null && !contentType.isEmpty()) {
                mediaType = MediaType.valueOf(contentType);
            } else {
                mediaType = MediaTypes.ANY;
            }
        }

        return mediaType;
    }

    private Map<String, Document<?>> getInputs(final Exchange theExchange) {
        Map<String, Document<?>> inputs = new HashMap<>();

        // Add the body as a Document.
        inputs.put("body", getBodyAsDocument(theExchange, getBodyMediaType(theExchange)));

        // Add all properties that start with the Datasonnet variable prefix.
        theExchange.getProperties().forEach((key, value) -> {
            if (key.startsWith(VARIABLE)) {
                // TODO: Add support for expressing the mediatype.
                inputs.put(key, getDocument(theExchange, MediaTypes.APPLICATION_JSON, value));
            }
        });

        // Add all headers that start with the Datasonnet variable prefix.
        theExchange.getIn().getHeaders().forEach((key, value) -> {
            if (key.startsWith(VARIABLE)) {
                // TODO: Add support for expressing the mediatype.
                inputs.put(key, getDocument(theExchange, MediaTypes.APPLICATION_JSON, value));
            }
        });

        return inputs;
    }

    /**
     * Resolve the imports for the Datasonnet expression. If the libraryPaths are set it will try to load the
     * DataSonnet libraries from those paths.
     *
     * @param theLanguage The {@link DatasonnetLanguage} to use for resolving the imports.
     * @return A {@link Map} containing the imports.
     */
    private Map<String, String> resolveImports(final DatasonnetLanguage theLanguage) {
        if (libraryPaths == null) {
            return theLanguage.getDatasonnetImports();
        }

        final Map<String, String> imports = new HashMap<>();
        log.debug("Explicit library path is: {}", libraryPaths);
        for (String nextPath : libraryPaths) {
            final File nextLibDir = new File(nextPath);
            if (nextLibDir.isDirectory()) {
                try {
                    Files.walkFileTree(nextLibDir.toPath(), new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            final File f = file.toFile();
                            if (!f.isDirectory() && f.getName().toLowerCase().endsWith(".libsonnet")) {
                                final String content = Files.readString(file.toAbsolutePath(), StandardCharsets.UTF_8);
                                final Path relative = nextLibDir.toPath().relativize(file);

                                log.debug("Loading DataSonnet library: {}", relative);

                                imports.put(relative.toString(), content);
                            }

                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    log.warn("Unable to load DataSonnet library from: {}", nextPath, e);
                }
            }
        }

        return imports;
    }

    @Override
    public String getExpressionText() {
        return expression;
    }

    @Override
    public Class<?> getResultType() {
        return resultType;
    }

    @Override
    public String toString() {
        return "datasonnet: " + getExpressionText();
    }
}
