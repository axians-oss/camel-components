package nl.axians.camel.language.datasonnet;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements an address line tokenizer that can be used to tokenize an address line into a list of tokens.
 */
public class AddressLineTokenizer {

    public static class Token {
        public enum Type {
            NUMBER,
            WORD_STARTING_WITH_DIGIT,
            WORD_NOT_STARTING_WITH_DIGIT
        }

        final Type type;
        final String text;
        final int startIndex;
        final int endIndex;

        public Token(Type type, String text, int startIndex, int endIndex) {
            this.type = type;
            this.text = text;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", text='" + text + '\'' +
                    ", startIndex=" + startIndex +
                    ", endIndex=" + endIndex +
                    '}';
        }
    }

    public static List<Token> tokenize(String theAddressLine) {
        final List<Token> tokens = new ArrayList<>();
        final String[] words = theAddressLine.split("\\s+");
        int currentIndex = 0;

        for (String word : words) {
            int startIndex = theAddressLine.indexOf(word, currentIndex);
            int endIndex = startIndex + word.length() - 1;

            if (word.matches("\\d+")) {
                tokens.add(new Token(Token.Type.NUMBER, word, startIndex, endIndex));
            } else if (word.matches("\\d\\S*")) {
                tokens.add(new Token(Token.Type.WORD_STARTING_WITH_DIGIT, word, startIndex, endIndex));
            } else {
                tokens.add(new Token(Token.Type.WORD_NOT_STARTING_WITH_DIGIT, word, startIndex, endIndex));
            }

            currentIndex = endIndex + 1;
        }

        return tokens;
    }

}