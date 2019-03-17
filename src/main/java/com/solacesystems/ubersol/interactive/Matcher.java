package com.solacesystems.ubersol.interactive;

import java.util.List;

/**
 * Takes care of simple parser matching of input tokens to commands.
 *
 * Also does basic syntax/args checks for each command match.
 */
class Matcher {
    private final List<String> words;
    private final String arg0;
    private final String arg1;
    
    Matcher(List<String> words) {
        this.words = words;
        this.arg0 = words.get(0);
        this.arg1 = (words.size()>1) ? words.get(1) : null;
    }

    boolean isExit() {
        return arg0.toLowerCase().matches("^exit$");
    }

    boolean isHelp() {
        return (arg0.toLowerCase().equals("help") || arg0.equals("?"));
    }

    boolean isShowConnections() {
        return (arg0.toLowerCase().equals("ls"));
    }

    boolean isConnect() throws IllegalArgumentException {
        if (arg1.charAt(0) == '+') {
            if (words.size() < 4)
                throw new IllegalArgumentException("Connect strings must have 4 args");
            return true;
        }
        return false;
    }

    boolean isSftpConnect() throws IllegalArgumentException {
        if (arg1.charAt(0) == '*') {
            if (words.size() < 4)
                throw new IllegalArgumentException("Connect strings must have 4 args");
            return true;
        }
        return false;
    }

    boolean isDisconnect() {
        return (arg1.charAt(0) == '-');
    }

    boolean isCLIExec() throws IllegalArgumentException {
        if (arg1.charAt(0) == '!') {
            if (words.size() < 3)
                throw new IllegalArgumentException("Script exec command must have 3 args");
            return true;
        }
        return false;
    }

    boolean isSftpGet() throws IllegalArgumentException {
        if (arg1.charAt(0) == '<') {
            if (words.size() < 4)
                throw new IllegalArgumentException("SFTP GET cmd missing an arg (try '? sftp')");
            return true;
        }
        return false;
    }

    boolean isSftpPut() throws IllegalArgumentException {
        if (arg1.charAt(0) == '>') {
            if (words.size() < 4)
                throw new IllegalArgumentException("SFTP PUT cmd missing an arg (try '? sftp')");
            return true;
        }
        return false;
    }

    boolean isSftpShow() {
        return (arg1.equals("ls"));
    }

    boolean isCLIContext() {
        return (arg1.equals("context"));
    }
}
