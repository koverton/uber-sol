package com.solacesystems.ubersol;

import java.util.List;
import java.util.Map;

/**
 * <p>Provides creation and caching of SFTP connections to various Solace appliances
 * and methods to execute ftp commands on those sessions.
 *
 * <p>Typically, one would use this tool as part of scripting rollouts across multiple appliances at a time.
 *
 * <p>All methods EXCEPT the connect() method take a 'glob-expression' of connections
 * the command is expected to execute over. For example, if the SftpExecutor currently has
 * 3 sessions open with names { fred, sally, sherman }, then the following upload cmd will be
 * executed on { sally, sherman } due to glob-matching:
 *
 * <p>{@code put("s*", "soltr_7.0.0.1078.tar.gz", "loads/");}
 *
 * <p>NOTE: this is glob-matching, NOT regex matching.
 */
public interface CLIExecutor {

    /**
     * Creates a CLI session via SSH and caches the session mapped to a connectionName string.
     *
     * @param connectionName Name (NOT a wildcard expression) assigned to this session for retrieval in
     *                       disconnect, execute, and executeScript commands.
     * @param ipAndPort The IP:port to connect to. If no port is provided, port 22 is assumed.
     * @param user Username to authenticate with.
     * @param password Password to authenticate with username.
     * @return The connection status and initial text output from the session upon connection.
     * @see com.solacesystems.ubersol.Response
     */
    Response connect(String connectionName, String ipAndPort, String user, String password);

    /**
     * Disconnects all CLI sessions with names connExpr matches via globbing and removes them from the session cache.
     *
     * @param connExpr A glob-expression intended to match 1 or more cached sessions for disconnection.
     * @return List of disconnection status and output for each matched connection.
     * @see com.solacesystems.ubersol.Response
     */
    Map<String, Response> disconnect(String connExpr);

    /**
     * Returns the list of all CLI session names currently cached.
     * @return names of all cached CLI sessions.
     */
    List<String> listSessions();

    Map<String, Response> listContext(String connExpr);

    /**
     * Executes an ad hoc Solace CLI command on all session with names connExpr matches via globbing.
     *
     * @param connExpr A glob-expression intended to match 1 or more cached sessions for command execution.
     * @param command  Ad hoc Solace CLI statement; this is passed directly to the attached Solace session without validaton.
     * @return List of execution status and output for each matched connection.
     * @see com.solacesystems.ubersol.Response
     */
    Map<String, Response> execute(String connExpr, String command);

    /**
     * Reads CLI commands from a local script file, executing them over all sessions with names matched against connExpr via globbing.
     *
     * @param connExpr A glob-expression intended to match 1 or more cached sessions for disconnection.
     * @param scriptFileName Path to local file containing Solace CLI commands; these commands are not validated before execution on all matching sessions.
     * @return List of script-execution status and output for each matched connection.
     * @see com.solacesystems.ubersol.Response
     */
    Map<String, Response> executeScript(String connExpr, String scriptFileName);
}
