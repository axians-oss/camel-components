package nl.axians.camel.language.datasonnet;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

import static nl.axians.camel.language.datasonnet.AddressLineTokenizer.Token.Type.*;

/**
 * This class implements an address line parser that can be used to translate an address line into a structured
 * address consisting out of a street, house number and house number addition.
 */
public class AddressLineParser {

    @AllArgsConstructor
    @ToString
    public static class Result {
        public String streetName;
        public Long houseNumber;
        public String houseNumberAddition;
    }

    /**
     * Parses the given address line and returns a structured address line of street, house number. and house number
     * addition.
     *
     * @param theAddressLine The address line to parse.
     * @return The structured address line.
     */
    public static Result parse(final String theAddressLine) {
        // Validate the input.
        if (theAddressLine == null || theAddressLine.isBlank()) {
            throw new IllegalArgumentException("The address line cannot be null or empty.");
        }

        // Tokenize the address line.
        final List<AddressLineTokenizer.Token> tokens = AddressLineTokenizer.tokenize(theAddressLine);
        final int lastTokenIdx = tokens.size() - 1;

        if (tokens.get(0).type == WORD_NOT_STARTING_WITH_DIGIT) {
            // StreetName ([HouseNumber] [Addition] || [HouseNumber+Addition])
            if (lastTokenIdx == 1) {
                if (tokens.get(1).type == NUMBER) {
                    final Long houseNumber = Long.parseLong(tokens.get(1).text);
                    final String streetName = tokens.get(0).text;
                    return new Result(streetName, houseNumber, null);
                } else if (tokens.get(1).type == WORD_STARTING_WITH_DIGIT) {
                    final String[] parts = splitHouseNumberAndAddition(tokens.get(0).text);
                    final Long houseNumber = (parts.length >= 1) ? Long.parseLong(parts[0]) : 0L;
                    final String addition = (parts.length >= 2) ? parts[1] : null;
                    final String streetName = tokens.get(0).text;
                    return new Result(streetName, houseNumber, addition);
                } else {
                    // Only street name.
                    return new Result(theAddressLine, null, null);
                }
            } else if (lastTokenIdx >= 2) {
                final AddressLineTokenizer.Token lastToken = tokens.get(lastTokenIdx);
                final AddressLineTokenizer.Token secondLastToken = tokens.get(lastTokenIdx - 1);

                if (secondLastToken.type == NUMBER) {
                    // StreetName HouseNumber Addition
                    final Long houseNumber = Long.parseLong(secondLastToken.text);
                    final String addition = lastToken.text;
                    final String streetName = theAddressLine.substring(0, secondLastToken.startIndex);
                    return new Result(streetName, houseNumber, addition);
                } else if (lastToken.type == WORD_STARTING_WITH_DIGIT) {
                    // StreetName HouseNumber+Addition
                    final String[] parts = splitHouseNumberAndAddition(tokens.get(0).text);
                    final Long houseNumber = (parts.length >= 1) ? Long.parseLong(parts[0]) : 0L;
                    final String addition = (parts.length >= 2) ? parts[1] : null;
                    final String streetName = theAddressLine.substring(0, lastToken.startIndex);
                    return new Result(streetName, houseNumber, addition);
                } else if (lastToken.type == NUMBER) {
                    // StreetName HouseNumber
                    final Long houseNumber = Long.parseLong(lastToken.text);
                    final String streetName = theAddressLine.substring(0, lastToken.startIndex);
                    return new Result(streetName, houseNumber, null);
                } else {
                    // Only street name.
                    return new Result(theAddressLine, null, null);
                }
            } else {
                // Only street name.
                final String streetName = tokens.get(0).text;
                return new Result(streetName, null, null);
            }
        } else if (tokens.get(0).type == NUMBER) {
            // HouseNumber StreetName [Addition]
            final Long houseNumber = Long.parseLong(tokens.get(0).text);
            final String addition = (tokens.get(lastTokenIdx).type == WORD_STARTING_WITH_DIGIT) ? tokens.get(lastTokenIdx).text : null;

            // Determine the start and end index of the street name.
            final int streetNameStartIdx = tokens.get(1).text.length() + 1;
            final int streetNameEndIdx = (addition != null) ? theAddressLine.length() - addition.length() - 1 : theAddressLine.length();
            if (streetNameEndIdx <= streetNameStartIdx) {
                throw new IllegalArgumentException("Invalid address line: Street name is missing (" + theAddressLine + ").");
            }

            final String streetName = theAddressLine.substring(streetNameStartIdx, streetNameEndIdx);

            return new Result(streetName, houseNumber, addition);
        } else if (tokens.get(0).type == WORD_STARTING_WITH_DIGIT) {
            // [HouseNumber+Addition] Street name
            final String[] parts = splitHouseNumberAndAddition(tokens.get(0).text);
            final Long houseNumber = (parts.length >= 1) ? Long.parseLong(parts[0]) : 0L;
            final String addition = (parts.length >= 2) ? parts[1] : null;
            final String streetName = theAddressLine.substring(tokens.get(0).endIndex + 1);
        }

        throw new IllegalArgumentException("Invalid address line: " + theAddressLine);
    }

    public static String[] splitHouseNumberAndAddition(String houseNumberAddition) {
        String[] parts = houseNumberAddition.split("(?<=\\d)(?=[^\\d])|(?<=[^\\d])(?=\\d)");
        return parts;
    }

}

