package nl.axians.camel.datasonnet.springboot;

import com.datasonnet.document.DefaultDocument;
import com.datasonnet.document.MediaType;
import com.datasonnet.document.MediaTypes;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConverters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Type converters for DataSonnet.
 */
public class DataSonnetTypeConverters implements TypeConverters {

    /**
     * Convert a {@link DefaultDocument} to a String.
     *
     * @param theDocument The theDocument to convert.
     * @return The content of the theDocument as a String.
     */
    @Converter
    public static String toString(final DefaultDocument<String> theDocument) {
        return theDocument.getContent();
    }

    /**
     * Convert a {@link DefaultDocument} to an InputStream.
     *
     * @param theDocument The theDocument to convert.
     * @return The content of the theDocument as an InputStream.
     */
    @Converter
    public static InputStream toInputStream(final DefaultDocument<String> theDocument) {
        return new ByteArrayInputStream(theDocument.getContent().getBytes(StandardCharsets.UTF_8));
    }

    @Converter
    public static DefaultDocument<?> toDefaultDocument(final String theContent,
                                                            final Exchange theExchange) {
        final String contentType = theExchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);
        final MediaType mediaType = (contentType != null && !contentType.isBlank()) ?
                MediaType.valueOf(contentType) : MediaTypes.APPLICATION_JSON;

        return new DefaultDocument<>(theContent, mediaType);
    }

    @Converter
    public static DefaultDocument<?> toDefaultDocument(final InputStream theContent,
                                                            final Exchange theExchange) throws IOException {
        return toDefaultDocument(new String(theContent.readAllBytes(), StandardCharsets.UTF_8), theExchange);
    }

}
