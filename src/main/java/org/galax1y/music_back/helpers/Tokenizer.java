package org.galax1y.music_back.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class Tokenizer {
    private static final String[] delimiters = { "BPM\\+", "R\\+", "R\\-", " " };
    private static final String regex = String.join("|", delimiters);

    public static String[] Tokenize(String raw) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(raw);

        List<String> result = new ArrayList<>();

        int lastIndex = 0;

        // Iterate over each match found by the regex
        while (matcher.find()) {
            // Add individual characters **before** the delimiter
            while (lastIndex < matcher.start()) {
                result.add(String.valueOf(raw.charAt(lastIndex))); // Adds one character at a time
                lastIndex++;
            }

            // Add the **delimiter itself** to the result list
            result.add(matcher.group());
            lastIndex = matcher.end(); // Move the index past the matched delimiter
        }

        // Handle remaining characters **after the last delimiter**
        while (lastIndex < raw.length()) {
            result.add(String.valueOf(raw.charAt(lastIndex))); // Adds remaining individual characters
            lastIndex++;
        }

        // Convert List to Array
        return result.toArray(new String[0]);
    }
}

