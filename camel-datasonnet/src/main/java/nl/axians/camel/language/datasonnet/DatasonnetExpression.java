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
import org.apache.camel.*;
import org.apache.camel.spi.ExpressionResultTypeAware;
import org.apache.camel.support.ExchangeHelper;
import org.apache.camel.support.ExpressionAdapter;
import org.apache.camel.util.IOHelper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Represents a Datasonnet expression.
 */
@Slf4j
public class DatasonnetExpression extends ExpressionAdapter implements ExpressionResultTypeAware {

    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^input body.*",
            java.util.regex.Pattern.MULTILINE);

    private final String expression;
    private final String name;

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

    @Getter
    @Setter
    private List<DataSonnetInput> inputs;

    @Getter
    @Setter
    private List<Library> libraries;

    /**
     * Create a new {@link DatasonnetExpression} with the given expression.
     *
     * @param theExpression The Datasonnet expression.
     */
    public DatasonnetExpression(final String theExpression) {
        name = theExpression;
        expression = loadResource(theExpression);
        libraries = new ArrayList<>();
    }


    private String loadResource(String theExpression) {
        if (theExpression.startsWith("resource:classpath:")) {
            final String resource = theExpression.substring("resource:classpath:".length());
            try (final InputStream is = getClass().getResourceAsStream(resource)) {
                if (is == null) {
                    throw new IllegalArgumentException("Resource not found: " + resource);
                }

                return IOHelper.loadText(is);
            } catch (IOException e) {
                throw new RuntimeCamelException("Error loading resource: " + resource, e);
            }
        }

        return theExpression;
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

        // Verify that the expression is not a property placeholder.
        // This means that the expression is not a constant expression and cannot be initialized upfront.
        if (expression.startsWith("${"))
            return;

        language.computeIfMiss(name, () -> {
            log.info("Initializing Datasonnet expression {}", name);
            return createDataSonnetMapper(expression);
        });
    }

    private Mapper createDataSonnetMapper(final String expression) {
        final Set<DataSonnetInput> allInputs = new HashSet<>();
        allInputs.add(DataSonnetInput.of("body", null));

        // Make sure we have input names.
        if (inputs != null) {
            allInputs.addAll(this.inputs);
        }

        final List<String> inputNames = allInputs.stream().map(DataSonnetInput::getName).toList();
        MapperBuilder builder = new MapperBuilder(expression)
                .withInputNames(inputNames)
                .withImports(resolveImports(language))
                .withDefaultOutput(MediaTypes.APPLICATION_JAVA);

        log.info("Adding libraries to Datasonnet expression: {}", libraries.size());
        for (Library lib : libraries) {
            log.info("Adding library: {}", lib.getClass().getSimpleName());
            builder = builder.withLibrary(lib);
        }

        return builder.build();
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
    public <T> T evaluate(
            final Exchange theExchange,
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
        final MediaType mediaTypeBody = getBodyMediaType(theExchange);
        final MediaType mediaTypeOutput = getOutputMediaType(theExchange);
        final Document<?> body = getBodyAsDocument(theExchange, mediaTypeBody);

        final Map<String, Document<?>> inputDocuments = getInputs(theExchange);
        inputDocuments.put("body", body);

        final Mapper mapper = language.lookup(name).orElseGet(() -> {
            if (name.startsWith("${")) {
                // The expression is a property placeholder, so we need to evaluate it first.
                final Expression camelExpression = theExchange.getContext().resolveLanguage("simple").createExpression(name);
                final String expr = camelExpression.evaluate(theExchange, String.class);
                return createDataSonnetMapper(expr);
            }

            throw new IllegalArgumentException("Datasonnet expression not found: " + name);
        });

        if (resultType == null || resultType.equals(Document.class)) {
            return mapper.transform(body, inputDocuments, mediaTypeOutput, Object.class);
        } else {
            return mapper.transform(body, inputDocuments, mediaTypeOutput, resultType);
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
                document = new DefaultDocument<>(text, theMediaType);
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

        if (expression.startsWith(Header.DATASONNET_HEADER)) {
            final Matcher matcher = PATTERN.matcher(expression);
            if (matcher.find()) {
                final String header = matcher.group(0);
                final String[] parts = header.split(" ");
                if (parts.length > 2) {
                    mediaType = MediaType.valueOf(parts[2]);
                }
            }
        }

        final String contentType = theExchange.getIn().getHeader(DatasonnetConstants.BODY_MEDIATYPE, String.class);
        if (contentType != null) {
            mediaType = MediaType.valueOf(contentType);
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

    /**
     * Get the inputs for the Datasonnet expression. This will include the body and all properties and headers that
     * start with the Datasonnet variable prefix.
     *
     * @param theExchange The exchange from which to get the inputs.
     * @return A {@link Map} containing the inputs.
     */
    private Map<String, Document<?>> getInputs(final Exchange theExchange) {
        final Map<String, Document<?>> inputDocuments = new HashMap<>();

        // Add all Exchange properties registered as inputs.
        if (inputs != null) {
            inputs.forEach(input -> {
                Object value = null;

                if (theExchange.getProperties().containsKey(input.getName())) {
                    value = theExchange.getProperties().get(input.getName());
                } else if (theExchange.getIn().getHeaders().containsKey(input.getName())) {
                    value = theExchange.getIn().getHeaders().get(input.getName());
                } else {
                    log.warn("DataSonnet input {} not found in exchange properties or headers. Setting value to null.", input.getName());
                }

                inputDocuments.put(input.getName(), getDocument(theExchange, input.getMediaType(), value));
            });
        }

        return inputDocuments;
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
                    Files.walkFileTree(nextLibDir.toPath(), new SimpleFileVisitor<>() {

                        @NotNull
                        @Override
                        public FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) throws IOException {
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
