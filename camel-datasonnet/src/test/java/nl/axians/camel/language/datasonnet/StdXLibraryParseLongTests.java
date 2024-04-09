package nl.axians.camel.language.datasonnet;

import com.datasonnet.Mapper;
import com.datasonnet.MapperBuilder;
import com.datasonnet.document.DefaultDocument;
import com.datasonnet.document.Document;
import com.datasonnet.document.MediaTypes;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class tests the functionality of the {@link StdXLibrary}.
 */
public class StdXLibraryParseLongTests {

    @Test
    public void testParseLongWithString() throws JSONException {
        // Arrange
        final Mapper mapper = getMapper();
        final Document<String> payload = new DefaultDocument<>("{\"number\": \"100000300508\"}",
                MediaTypes.APPLICATION_JSON);

        // Act
        final Document<String> result = mapper.transform(payload);

        // Assert
        assertThat(result).isNotNull();
        JSONAssert.assertEquals("{\"result\": 100000300508}", result.getContent(), false);
    }

    @Test
    public void testParseLongWithNumber() throws JSONException {
        // Arrange
        final Mapper mapper = getMapper();
        final Document<String> payload = new DefaultDocument<>("{\"number\": 100000300508}",
                MediaTypes.APPLICATION_JSON);

        // Act
        final Document<String> result = mapper.transform(payload);

        // Assert
        assertThat(result).isNotNull();
        JSONAssert.assertEquals("{\"result\": 100000300508}", result.getContent(), false);
    }

    @Test
    public void testParseLongWithNull() throws JSONException {
        // Arrange
        final Mapper mapper = getMapper();
        final Document<String> payload = new DefaultDocument<>("{\"number\": null}",
                MediaTypes.APPLICATION_JSON);

        // Act
        final Document<String> result = mapper.transform(payload);

        // Assert
        assertThat(result).isNotNull();
        JSONAssert.assertEquals("{\"result\": null}", result.getContent(), false);
    }

    @Test
    public void testParseLongWithEmptyString() throws JSONException {
        // Arrange
        final Mapper mapper = getMapper();
        final Document<String> payload = new DefaultDocument<>("{\"number\": \"\"}",
                MediaTypes.APPLICATION_JSON);

        // Act
        final Document<String> result = mapper.transform(payload);

        // Assert
        assertThat(result).isNotNull();
        JSONAssert.assertEquals("{\"result\": null}", result.getContent(), false);
    }

    @Test
    public void testParseLongWithNumberException() {
        // Arrange
        final Mapper mapper = getMapper();
        final Document<String> payload = new DefaultDocument<>("{\"number\": \"illegal\"}",
                MediaTypes.APPLICATION_JSON);

        // Act & Assert
        assertThatThrownBy(() -> mapper.transform(payload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("For input string: \"illegal\"");
    }

    @Test
    public void testParseLongWithIllegalArgument() {
        // Arrange
        final Mapper mapper = getMapper();
        final Document<String> payload = new DefaultDocument<>("{\"number\": true}",
                MediaTypes.APPLICATION_JSON);

        // Act & Assert
        assertThatThrownBy(() -> mapper.transform(payload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Argument must be a valid number or a string representation of a number");
    }

    /**
     * Returns a {@link Mapper} that can be used to test the functionality of the {@link StdXLibrary}.
     *
     * @return The {@link Mapper} that can be used to test the functionality of the {@link StdXLibrary}
     */
    private static Mapper getMapper() {
        final StdXLibrary library = StdXLibrary.getInstance();
        return new MapperBuilder("{\"result\": stdx.parseLong(payload.number)}")
                .withLibrary(library)
                .withDefaultOutput(MediaTypes.APPLICATION_JSON)
                .build();
    }

}
