package nl.axians.camel.datasonnet.springboot;

import com.datasonnet.document.DefaultDocument;
import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Type converters for DataSonnet.
 */
public class DataSonnetTypeConverters implements TypeConverters {

    /**
     * Convert a {@link DefaultDocument} to a String.
     *
     * @param document The document to convert.
     * @return The content of the document as a String.
     */
    @Converter
    public static String toString(DefaultDocument<String> document) {
        return document.getContent();
    }

    /**
     * Convert a {@link DefaultDocument} to an InputStream.
     *
     * @param document The document to convert.
     * @return The content of the document as an InputStream.
     */
    @Converter
    public static InputStream toInputStream(DefaultDocument<String> document) {
        return new ByteArrayInputStream(document.getContent().getBytes(StandardCharsets.UTF_8));
    }

}
