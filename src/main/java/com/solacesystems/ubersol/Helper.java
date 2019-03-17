package com.solacesystems.ubersol;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Helper {

    /**
     * Splits the host portion from the front of a string in the expected format 'host:port'.
     *
     * @param ipAndPort IP-address with optional port in the format 'host:port'.
     * @return Just the host portion of the string if there is a port; if no port, the entire {@code ipAndPort} string is returned
     */
    static int getPort(String ipAndPort) {
        if (ipAndPort.lastIndexOf(':') > -1) {
            return Integer.parseInt(ipAndPort.substring(ipAndPort.lastIndexOf(':')+1));
        }
        return 22;
    }

    /**
     * Splits the port portion from the end of a string in the expected format 'host:port'.
     *
     * @param ipAndPort IP-address with optional port in the format 'host:port'.
     * @return Just the port integer of the string if there is a port; if no port, 22 is returned (standard SSH port).
     */
    static String getHost(String ipAndPort) {
        if (ipAndPort.lastIndexOf(':') > -1) {
            return ipAndPort.substring(0, ipAndPort.lastIndexOf(':'));
        }
        return ipAndPort;
    }

    /**
     * Reads CLI session output the splits the output from the prompt value, returning both in a string array
     * in the order [ response, prompt ].
     * @param reader InputReader connected to an SSH session on a Solace appliance
     * @return 2-member String array containing the prompt string followed by the response lines all in one string
     * @throws IOException failure attempting to read the response stream.
     */
    static String[] readResponse(InputStreamReader reader) throws IOException {
        String rawResponse = readBuffer(reader);
        return removePrompt(rawResponse);
    }


    /**
     * Perform Unix-style glob-matching
     * @param domain List of strings to be matched against
     * @param globexpr Wildcard/glob expression to be evaluated across the {@code domain} input parameter
     * @return result set of matched objects
     */
    static List<String> glob(Set<String> domain, String globexpr) {
        List<String> result = new ArrayList<>();
        // We really want unix-style globs, not perl-style regex.
        // So with that assumption, when user enters 'd*' this
        // code translates that into the regex 'd.*' before matching.
        String regex = globexpr.replaceAll("\\*", ".*");
        for(String word : domain) {
            if (word.matches(regex))
                result.add(word);
        }
        return result;
    }

    /**
     * Finds the last location of any character within the array {@code any} in the input String {@code s}
     * @param s String to be searched
     * @param any List of 'stop characters' to search for in {@code s}
     * @return Integer index of the first match found when starting at the end of the string.
     */
    private static int lastIndexOfAny(String s, char[] any) {
        int p = s.length()-1;
        while(p >= 0) {
            for(char c : any) {
                if (s.charAt(p) == c) return p;
            }
            p--;
        }
        return -1;
    }

    private static String[] removePrompt(String output) {
        int gtpos = lastIndexOfAny(output, new char[]{'>', '#'});
        if (gtpos > -1) {
            int nlpos = output.lastIndexOf('\n', gtpos);
            if (nlpos > -1) {
                return new String[] { output.substring(nlpos).trim(), output.substring(0, nlpos).trim() };
            }
        }
        return new String[] { "", output };
    }

    /**
     * Gathers input from {@code reader} until a Solace CLI prompt is discovered. This is context-specific
     * to the CLI interface for Solace Messaging Appliances and will hang for any other source of input!
     *
     * @param reader InputReader connected to an SSH session on a Solace appliance
     * @return All output read from the {@code reader} including the final prompt.
     * @throws IOException failure attempting to read input stream.
     */
    private static String readBuffer(InputStreamReader reader) throws IOException {
        StringBuffer response = new StringBuffer();
        char[] buf = new char[1024];
        int len;
        boolean done;
        do {
            len = reader.read(buf, 0, 1024);
            if (len > 0) {
                response.append(buf, 0, len);
                done = hasPrompt(response) | requiresInput(response.toString());
            }
            else if (len < 0) {
                // TODO: Socket closed! Now what!?
                done = true;
            }
            else {
                done = true;
            }
        }
        while(!done);
        return response.toString();
    }

    static boolean hasPrompt(StringBuffer sb) {
        int len = sb.length();
        return len>2 && (sb.charAt(len-2)=='>' || sb.charAt(len-2)=='#') && sb.charAt(len-1)==' ';
    }

    static boolean requiresInput(String response) {
        return response.trim().contains("(y/n)?");
    }
}
