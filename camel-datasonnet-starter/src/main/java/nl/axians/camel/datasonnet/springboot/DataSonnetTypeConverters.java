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
     * Convert a {@link DefaultDocument} to an InputStream.
     *
     * @param theDocument The theDocument to convert.
     * @return The content of the theDocument as an InputStream.
     */
    @Converter
    public InputStream toInputStream(final DefaultDocument<String> theDocument) {
        return new ByteArrayInputStream(theDocument.getContent().getBytes(StandardCharsets.UTF_8));
    }

}
