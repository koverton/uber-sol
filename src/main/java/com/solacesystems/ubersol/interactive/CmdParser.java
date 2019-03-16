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

        // One word answers
        String arg0 = words.get(0);
        // Exit request
        if (arg0.toLowerCase().matches("^exit$"))
            return new Cmd(OP.Exit, "");
        // Help Requests
        if (arg0.toLowerCase().equals("help") || arg0.equals("?")) {
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
        if (arg0.toLowerCase().equals("ls")) {
            // Show connections
            return new ShowConnsCmd(name);
        }
        // No such thing
        if (words.size() == 1)
            return parseError();

        String arg1 = words.get(1);

        // Connect a new CLI session
        if (arg1.charAt(0) == '+') {
            if (words.size() < 4)
                return parseError();
            return new ConnectCmd(arg0, words.get(2), words.get(3), false);
        }
        // Connect a new SFTP session
        else if (arg1.charAt(0) == '*') {
            if (words.size() < 4)
                return parseError();
            return new ConnectCmd(arg0, words.get(2), words.get(3), true);
        }
        // Disconnect named session(s)
        if (arg1.charAt(0) == '-') {
            return new Cmd(OP.Disconnect, arg0);
        }
        // Execute a local script on remote sessions
        if (arg1.charAt(0) == '!') {
            if (words.size() < 3)
                return parseError();
            String arg2 = words.get(2);
            File file = new File(arg2);
            if (!file.exists() || !file.canRead()) {
                return new ErrorCmd(arg2, "Script file does not exist or is not readable. Please check the file and try again.");
            }
            return new RunScriptCmd(arg0, arg2);
        }
        // SFTP GET
        if (arg1.charAt(0) == '<') {
            if (words.size() < 4)
                return new ErrorCmd(name, "SFTP GET cmd missing an arg (try '? sftp')");
            return SftpCmd.GET(arg0, words.get(2), words.get(3));
        }
        // SFTP PUT
        if (arg1.charAt(0) == '>') {
            if (words.size() < 4)
                return new ErrorCmd(name, "SFTP PUT cmd missing an arg (try '? sftp')");
            return SftpCmd.PUT(arg0, words.get(2), words.get(3));
        }
        // SFTP SHOW
        if (arg1.equals("ls")) {
            words.remove(0);
            words.remove(0);
            return SftpCmd.SHOW(arg0, join(words));
        }

        // Assume the rest are Solace CLI commands (and let solace complain if they aren't)
        words.remove(0);
        return new ExecCmd(arg0, join(words));
    }

    private ErrorCmd parseError() {
        return new ErrorCmd(name, HelpMsg.USAGE);
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
