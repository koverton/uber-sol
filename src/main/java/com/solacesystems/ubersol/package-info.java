/**
 * <p>This package provides components which can execute Solace CLI operations on several remote appliances
 * via SSH sessions to those appliances. The basic operations available are:
 * <ul>
 *     <li>Create and cache an SSH session</li>
 *     <li>Disconnect cached SSH sessions</li>
 *     <li>Execute ad hoc CLI commands in SSH sessions</li>
 *     <li>Read CLI scripts from local files and execute them in SSH sessions</li>
 * </ul>
 *
 * <p>The most interesting thing about this interface is the use of 'named connections' and globbing on these
 * connection names. This allows any script or ad hoc command to be executed across multiple SSH sessions.
 *
 * <p>For example, if I have opened sessions to multiple appliances and named those sessions
 * {@code jimmy}, {@code jack} and {@code gunther};
 * then by passing in a connection-expression of {@code j*} to the {@link com.solacesystems.ubersol.CLIExecutor#execute} method
 * allows me to execute my command on sessions {@code jimmy}, {@code jack}. If I passed {@code *} the command would execute on all
 * live sessions.
 */
package com.solacesystems.ubersol;
