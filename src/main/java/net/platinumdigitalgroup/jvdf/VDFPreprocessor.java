/*
Copyright 2017 Platinum Digital Group LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package net.platinumdigitalgroup.jvdf;

import java.util.stream.IntStream;

/**
 * The VDF preprocessor transforms valid, human-readable VDF into minified, less-than-humanly readable VDF that is
 * more easily parsed.  This two-stage process allows for far easier parallelization, as each line can be mapped to a
 * worker thread.
 * @author Brendan Heinonen
 */
public class VDFPreprocessor {

    /**
     * Preprocesses a VDF document into a minified, less-than-humanly readable, but still valid VDF document with
     * comments and unnecessary whitepsaces removed..
     * @param vdf the VDF document to process
     * @return a VDF document transformed from the input document
     */
    public String process(String vdf) {
        return process(vdf.split("\\n"));
    }

    /**
     * Preprocesses a VDF document into a minified, less-than-humanly readable, but still valid VDF document with
     * comments and unnecessary whitepsaces removed..
     * @param lines an array of lines of a VDF document to process
     * @return a VDF document transformed from the input document
     */
    public String process(String[] lines) {
        // Execute line processors on each line
        processLines(lines);

        // Rebuild output VDF
        StringBuilder builder = new StringBuilder(lines.length * 20);
        for (int i = 0, linesLength = lines.length; i < linesLength; i++) {
            String s = lines[i];
            if (s != null && s.length() > 0) {
                builder.append(s);

                if(i < linesLength - 1)
                    builder.append(" ");
            }
        }
        return builder.toString();
    }

    /**
     * Preprocesses an array of lines in a VDF document, and stores the resultant processed lines back into the array.
     * @param lines the lines to process
     */
    public void processLines(String[] lines) {
        IntStream.range(0, lines.length)
                .parallel()
                .forEach(i -> lines[i] = processLine(lines[i]));
    }

    /**
     * Preprocesses a single line in a VDF document.
     * @param line the original line to process
     * @return the line after it has been processed
     */
    public String processLine(final String line) {
        /*
         * I understand that this is control-flow spaghetti. This was originally written far cleaner, but was
         * significantly changed to a single iteration for performance reasons.
         */

        // Pre-warm StringBuilder with the original line length for fewer allocations
        StringBuilder sb = new StringBuilder(line.length());

        char[] charArray = line.toCharArray();

        // If the first characters of a line are a comment, we can immediately discard it
        if(charArray.length >= 2 && isComment(charArray, 0)) {
            return null;
        }

        // Whether a word character has been hit yet in this loop
        boolean hitWord = false;

        // Whether the line currently has unclosed quotes
        boolean openQuotes = false;

        // Iterate character array
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];
            char n = 0;
            char p = 0;
            boolean hasNext = i < charArrayLength - 1;
            boolean hasPrevious = i > 0;

            if(c == '\n' || c == '\r')
                continue;

            if(hasPrevious)
                p = charArray[i - 1];


            // Toggle open quote flag if we've encountered an unescaped quote
            if((c == '"' && !hasPrevious) || (hasPrevious && c == '"' && p != '\\'))
                openQuotes = !openQuotes;


            // Strip C-style comments
            if(hasNext) {
                n = charArray[i + 1];

                // If we're not in quotes and this is a comment, immediately return from this line
                if (!openQuotes && isComment(charArray, i)) {
                    return sb.toString();
                }
            }

            // Strip conditional statement
            if(!openQuotes && c == '[')
                return sb.toString();

            // Strip whitespace
            if(isWhitespace(c)) {

                if(!hitWord) {
                    // If we haven't hit a word character yet, don't include this whitespace
                    // This essentially trims whitespace from the beginning of the line
                    continue;
                } else {

                    // Skip whitespace characters in between words
                    if(hasNext && isWhitespace(n)) {
                        continue;
                    }

                    // Check if the rest of the line is whitespace. If so, we can immediately break out of the line.
                    boolean brk = false;

                    // Iterate the rest of the line. If it hits a non-whitespace character, it will break.
                    for(int j = i; j < charArrayLength && isWhitespace(charArray[j]); j++) {
                        // If we've made it to the end, that means the rest of the line is whitespace
                        if(j == charArrayLength - 1)
                            brk = true;
                    }

                    if(brk)
                        break;
                }

                // Replace whatever whitespace character this was with a space
                sb.append(' ');
            } else {
                // We've hit a word character
                hitWord = true;
                sb.append(c);
            }

        }

        return sb.toString();
    }

    /**
     * Determines whether or not a character sequence is a VDF comment.  VDF comments are C-style comments, except that
     * the comment will always take up the entire rest of the line. For that reason, block termination does not need to
     * be checked.
     * @param f the first character to test
     * @param s the second character to test, which must be immediately after f
     * @return if the two characters represent a VDF, C-style comment
     */
    private boolean isComment(char[] arr, int index) {
        char f = arr[index];
        char s = arr[index + 1];

        return f == '/' && (s == '*' || s == '/');
    }

    /**
     * Determines whether or not a character is considered a VDF whitespace character. According to the VDF spec,
     * whitespace characters include space, return, newline, and tab. Since the preprocessor will immediately strip
     * newline/returns, only space, tab, and vertical tab need be checked.
     * @param c the character to test
     * @return if the character is considered VDF whitespace character
     */
    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == 0x0B;
    }

}
