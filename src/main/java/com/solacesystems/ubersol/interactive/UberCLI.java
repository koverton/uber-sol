package com.solacesystems.ubersol.interactive;

import com.solacesystems.ubersol.*;

import java.util.HashMap;
import java.util.Map;

import static com.solacesystems.ubersol.interactive.Helper.*;
import static java.lang.System.out;

/**
 * Provides basic interactive stdin/stdout command execution across multiple SSH sessions. {@link UberCLI}
 * handles input parsing and output presentation, using a {@link com.solacesystems.ubersol.CLIExecutor} instance
 * to actually execute commands over the sessions.
 */
public class UberCLI {
    static final String NAME = "ubersol";

    /**
     * Instantiates and runs the {@link UberCLI} instance. This is the jar main class entrypoint.
     * @param args Ignored; no arguments required.
     */
    static public void main(String[] args) {
        UberCLI ubercli = new UberCLI();
        ubercli.run();
        ubercli.shutdown();
    }

    /**
     * Constructs the main instance including {@link Parser} and {@link com.solacesystems.ubersol.CLIExecutor} instances.
     */
    public UberCLI() {
        parser = new CmdParser();
        cliexec = new CLIExecutorImpl(NAME);
        sftpexec= new SftpExecutorImpl();
    }

    /**
     * Drives the main event loop for interaction with user via stdin/stdout. This will block until the
     * user terminates the session via the {@code exit} command.
     */
    public void run() {
        Cmd cmd;

        prompt();
        showProgress("READY.\n");

        do {
            prompt();

            cmd = parser.next();

            if (cmd != null) {
                for(Map.Entry<String,Response> entry : runCmd(cmd).entrySet())
                    showResponse(entry.getKey(), entry.getValue());
            }
        } while (cmd == null || cmd.getOp() != OP.Exit);
    }

    /**
     * Provides cleanup logic for all contained resources. Will close and destroy all live SSH sessions.
     */
    public void  shutdown() {
        cliexec.disconnect("*");
        sftpexec.disconnect("*");
    }

    private Map<String, Response> runCmd(Cmd cmd) {
        Map<String, Response> results;
        final String dest = cmd.getName();
        String password;
        switch(cmd.getOp()) {
            case CLIConnect:
                ConnectCmd cliConnect = (ConnectCmd) cmd;
                showProgress("To connect to CLI:"+dest+", please enter password:");
                password = parser.getPasswordSafe();
                showProgress("Thanks! Connecting ...");
                results = new HashMap<>();
                results.put(dest,
                        cliexec.connect(dest, cliConnect.getIpAndPort(), cliConnect.getUser(), password));
                break;
            case SFTPConnect:
                ConnectCmd sftpConnect = (ConnectCmd) cmd;
                showProgress("To connect to sftp:"+dest+", please enter password:");
                password = parser.getPasswordSafe();
                showProgress("Thanks! Connecting ...");
                results = new HashMap<>();
                results.put(dest,
                        sftpexec.connect(dest, sftpConnect.getIpAndPort(), sftpConnect.getUser(), password));
                break;
            case Disconnect:
                showProgress("(Disconnecting from "+dest+"...)");
                results = cliexec.disconnect(dest);
                results.putAll( sftpexec.disconnect(dest) );
                break;
            case ShowConns:
                showProgress("Showing all connections ... ");
                results = new HashMap<>();
                results.put("\tCLI", new Response(true,
                        "[ "+String.join(", ", cliexec.listSessions())+" ]", dest));
                results.put("\tSFTP", new Response(true,
                        "[ "+String.join(", ", sftpexec.listSessions())+" ]", dest));
                break;
            case ExecCLI:
                ExecCmd execCmd = (ExecCmd) cmd;
                showProgress("(Executing CLI on sessions matching '"+dest+"'...)");
                results = cliexec.execute(dest, execCmd.getCmd());
                break;
            case RunCLIScript:
                RunScriptCmd scriptCmd = (RunScriptCmd) cmd;
                showProgress("(Executing CLI script " + scriptCmd.getScriptName() + " on " + dest + "...)");
                results = cliexec.executeScript(dest, scriptCmd.getScriptName());
                break;
            case SftpGet:
                SftpCmd getCmd = (SftpCmd)cmd;
                showProgress("Downloading " + getCmd.getArg1() + " from " + dest+"...");
                results = sftpexec.get(getCmd.getName(), getCmd.getArg1(), getCmd.getArg2());
                break;
            case SftpPut:
                SftpCmd putCmd = (SftpCmd)cmd;
                showProgress("Uploading " + putCmd.getArg1() + " from " + dest+"...");
                results = sftpexec.get(putCmd.getName(), putCmd.getArg1(), putCmd.getArg2());
                break;
            case SftpShow:
                SftpCmd showCmd = (SftpCmd)cmd;
                showProgress("Listing files in " + showCmd.getArg1() + " from " + dest+"...");
                results = sftpexec.dir(showCmd.getName(), showCmd.getArg1());
                break;
            case Error:
                ErrorCmd errorCmd = (ErrorCmd) cmd;
                results = new HashMap<>();
                results.put(dest, new Response(false, errorCmd.getMessage(), "?????>"));
                break;
            case Help:
                HelpCmd helpCmd = (HelpCmd) cmd;
                results = new HashMap<>();
                results.put(dest, new Response(false, helpCmd.getMessage(), "help>"));
                break;
            case Exit:
                results = new HashMap<>();
                results.put(dest, new Response(true, "Goodbye!", NAME));
                break;
            default:
                results = new HashMap<>();
                results.put(dest, new Response(true, "???", NAME));
                break;
        }
        return results;
    }

    private static void prompt() {
        out.write('\n');
        out.print(NAME);
        out.print("> ");
        out.flush();
    }

    private static void showResponse(String connName, Response resp) {
        out.print(prettifyOutput(connName, resp.getOutput()));
    }

    private static void showProgress(String message) {
        out.print('\t');
        out.print(message);
        out.flush();
    }

    private final Parser parser;
    private final CLIExecutor cliexec;
    private final SftpExecutor sftpexec;
}
