package nl.axians.camel.language.datasonnet;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class AddressLineParserTest {

    @Test
    public void shouldParseAddressLines() throws Exception {
        // Arrange
        String json = new String(Files.readAllBytes(Paths.get(getClass().getResource("/address-lines.json").toURI())));
        List<String> addressLines = JsonPath.parse(json).read("$.value[*].address1_line1");

        // Act
        for (String addressLine : addressLines) {
            if (addressLine == null || addressLine.isBlank())
                continue;

            log.info("Parsing address line: {}", addressLine);
            List<AddressLineTokenizer.Token> tokens = AddressLineTokenizer.tokenize(addressLine);
            log.info("Parsed address line: {}", tokens);

            // Assert
            // Assertions.assertNotNull(result.houseNumber);
            // houseNumberAddition can be null, so no assertion for it
        }
    }
}