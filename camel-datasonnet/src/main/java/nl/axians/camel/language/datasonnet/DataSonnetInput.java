package nl.axians.camel.language.datasonnet;

import com.datasonnet.document.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a DataSonnet input.
 */
@Getter
@Setter
@Builder(toBuilder = true, setterPrefix = "with")
public class DataSonnetInput {

    private String name;
    private MediaType mediaType;

    /**
     * Creates a new DataSonnet input.
     *
     * @param name The name.
     * @param mediaType The media type.
     * @return The DataSonnet input.
     */
    public static DataSonnetInput of(
            @NotNull final String name,
            final MediaType mediaType) {
        return DataSonnetInput.builder()
                .withName(name)
                .withMediaType(mediaType)
                .build();
    }

}
