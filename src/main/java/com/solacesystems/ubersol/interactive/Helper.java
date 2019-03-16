package com.solacesystems.ubersol.interactive;

import java.util.ArrayList;
import java.util.List;

class Helper {

    static String prettifyOutput(String connectionName, String output) {
        String prompt = "\n"+connectionName+"> ";
        return prompt + output.replaceAll("[\n]", prompt);
    }

    static List<String> splitRespectingSpecialChars(String line, char[] specials) {
        List<String> words = new ArrayList<>();
        for(String w : line.trim().split("[ \t]+")) {
            if (w != null && w.length() > 0) {
                if (words.size() < 1)
                    separateSpecials(w, specials, words);
                else
                    words.add(w);
            }
        }
        return words;
    }
    private static void separateSpecials(String word, char[] specials, List<String> words) {
        // Make sure special chars are surrounded by space so they appear in the list as a single token
        for(char sc : specials) {
            word = word.replaceAll("["+sc+"]", " "+sc+" ");
        }
        for(String w : word.trim().split("[ \t]+")) {
            if (w != null && w.length() > 0)
                words.add(w);
        }
    }

    static String join(List<String> words) {
        StringBuilder sb = new StringBuilder();
        for(String w : words) {
            sb.append(w).append(' ');
        }
        return sb.toString().trim();
    }
}
