package com.solacesystems.ubersol;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides creation and caching of SFTP connections to various Solace appliances
 * and methods to execute ftp commands on those sessions.
 *
 * Typically, one would use this tool as part of scripting rollouts across multiple appliances at a time.
 *
 * All methods EXCEPT the connect() method take a 'glob-expression' of connections
 * the command is expected to execute over. For example, if the SftpExecutor currently has
 * 3 sessions open with names { fred, sally, sherman }, then the following upload cmd will be
 * executed on { sally, sherman } due to glob-matching:
 *
 * {@code put("s*", "soltr_7.0.0.1078.tar.gz", "loads/");}
 *
 * NOTE: this is glob-matching, NOT regex matching.
 */
public interface SftpExecutor {
    /**
     * Creates an SFTP session and caches the session mapped to a connectionName string.
     * For Solace appliances, FTP-Users have different credentials than CLI users, so please note
     * that sessions authenticated with CLI credentials will NOT succeed at executing SFTP operations.
     *
     * @param connectionName Name (NOT a wildcard expression) assigned to this session for retrieval in
     *                       disconnect, execute, and executeScript commands.
     * @param ipAndPort The IP:port to connect to. If no port is provided, port 22 is assumed.
     * @param user Username to authenticate with.
     * @param password Password to authenticate with username.
     * @return The connection status and initial text output from the session upon connection.
     * @see com.solacesystems.ubersol.Response
     */
    public Response connect(final String connectionName, final String ipAndPort, final String user, final String password);

    /**
     * Returns the list of all SFTP session names currently cached.
     * @return names of all cached SFTP sessions.
     */
    List<String> listSessions();

    /**
     * Disconnects all SFTP sessions with names connExpr matches via globbing and removes them from the session cache.
     *
     * @param connExpr A glob-expression intended to match 1 or more cached sessions for disconnection.
     * @return List of disconnection status and output for each matched connection.
     * @see com.solacesystems.ubersol.Response
     */
    public Map<String, Response> disconnect(final String connExpr);

    /**
     * Downloads files matching a specified remote-path on all matching remote SFTP connections,
     * and stores them in the specified local directory.
     *
     * @param connExpr A glob-expression intended to match 1 or more cached sessions for download.
     * @param remotePath SFTP-compliant remote path specification for downloads
     * @param localPath Local path to directory in which all downloaded files will be stored
     * @return List of download status and output for each matched connection.
     * @see com.solacesystems.ubersol.Response
     */
    public Map<String, Response> get(final String connExpr, final String remotePath, final String localPath);

    /**
     * Uploads local files to matching remote SFTP connections, and stores them in the specified local directory.
     *
     * @param connExpr A glob-expression intended to match 1 or more cached sessions for upload.
     * @param remotePath SFTP-compliant remote path specification for uploads
     * @param localPath Local path to files for upload
     * @return List of upload status and output for each matched connection.
     * @see com.solacesystems.ubersol.Response
     */
    public Map<String, Response> put(final String connExpr, final String remotePath, final String localPath);

    /**
     * Lists files on remote servers matching a specified path on the remote server.
     *
     * @param connExpr A glob-expression intended to match 1 or more cached sessions for disconnection.
     * @param remotePath Pathname to search for files on the remote SFTP server; supports
     * @return List of status and matching files for each matched connection.
     * @see com.solacesystems.ubersol.Response
     */
    public Map<String, Response> dir(final String connExpr, final String remotePath);
}

