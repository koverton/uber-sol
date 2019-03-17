package com.solacesystems.ubersol.interactive;

import java.io.*;
import java.util.List;

import static com.solacesystems.ubersol.interactive.Helper.*;

class CmdParser implements Parser {

    public CmdParser() {
        this.name = UberCLI.NAME;
        this.cons = System.console();
        if (cons == null)
            this.reader = new BufferedReader(new InputStreamReader(System.in));
        else
            this.reader = null;
        specialChars = new char[] { '+', '-', '!', '<', '>' };
    }

    public Cmd next() {
        Cmd cmd;
        try {
            String line = readLine();
            cmd = parse(line.trim());
        }
        catch(IOException e) {
            cmd = new ErrorCmd(name, e.getMessage());
        }
        return cmd;
    }

    Cmd parse(String line) {
        List<String> words = splitRespectingSpecialChars(line, specialChars);

        // User just hit return
        if (words.size() == 0)
            return null;

        Matcher matcher = new Matcher(words);
        // One word answers
        if (matcher.isExit())
            return new Cmd(OP.Exit, "");
        if (matcher.isHelp()) {
            if (words.size() == 1)
                return new HelpCmd(name, HelpMsg.TOP_LEVEL);
            if (words.get(1).matches("^co.*"))
                return new HelpCmd(name, HelpMsg.CONNECT);
            if (words.get(1).matches("^cl.*"))
                return new HelpCmd(name, HelpMsg.CLIEXEC);
            if (words.get(1).matches("^sf.*"))
                return new HelpCmd(name, HelpMsg.SFTPEXEC);
            else
                return new HelpCmd(name, HelpMsg.TOP_LEVEL);
        }
        if (matcher.isShowConnections()) {
            return new ShowConnsCmd(name);
        }
        // No other one-word answers, so the rest are parser errors
        if (words.size() == 1)
            return new ErrorCmd(name, HelpMsg.USAGE);

        // Everything below has at least 2 arguments
        String arg0 = words.get(0);
        try {
            if (matcher.isConnect()) {
                return new ConnectCmd(arg0, words.get(2), words.get(3), false);
            }
            else if (matcher.isSftpConnect()) {
                return new ConnectCmd(arg0, words.get(2), words.get(3), true);
            }
            if (matcher.isDisconnect()) {
                return new Cmd(OP.Disconnect, arg0);
            }
            if (matcher.isCLIExec()) {
                String arg2 = words.get(2);
                File file = new File(arg2);
                if (!file.exists() || !file.canRead()) {
                    return new ErrorCmd(arg2, "Script file does not exist or is not readable. Please check the file and try again.");
                }
                return new RunScriptCmd(arg0, arg2);
            }
            if (matcher.isSftpGet()) {
                return SftpCmd.GET(arg0, words.get(2), words.get(3));
            }
            if (matcher.isSftpPut()) {
                return SftpCmd.PUT(arg0, words.get(2), words.get(3));
            }
            if (matcher.isSftpShow()) {
                return SftpCmd.SHOW(arg0, join(words.subList(2, words.size())));
            }
            if (matcher.isCLIContext()) {
                return new ContextCmd(arg0);
            }
        }
        catch(IllegalArgumentException e) {
            return new ErrorCmd(name, e.getMessage());
        }

        // Assume the rest are Solace CLI commands (and let solace complain if they aren't)
        return new ExecCmd(arg0, join(words.subList(1, words.size())));
    }

    private String readLine() throws IOException {
        if (this.cons != null)
            return this.cons.readLine();
        else
            return this.reader.readLine();
    }

    @Override
    public String getPasswordSafe() {
        String passwd = null;

        if (cons != null) {
            char[] chars = cons.readPassword();
            passwd = new String(chars);
        }
        else {
            System.out.print("\n\tTrouble reading password (you're probably in an IDE); please enter in the clear: ");
            System.out.flush();
            try {
                passwd = reader.readLine();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        return passwd;
    }

    private final BufferedReader reader;
    private final String name;
    private final char[] specialChars;
    private final Console cons;
}
