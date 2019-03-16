package com.solacesystems.ubersol;

/**
 * Encapsulates the server response to a Connection, Command Execution, or Script Execution via a {@link com.solacesystems.ubersol.CLIExecutor}.
 *
 * Includes:
 * <ul>
 *     <li>Success / fail status</li>
 *     <li>Output from the server</li>
 *     <li>Server prompt after the operation</li>
 * </ul>
 */
public class Response {
    public Response(boolean success, String output, String prompt) {
        this.success = success;
        this.output = output;
        this.prompt = prompt;
    }

    /**
     * Pass/Fail status of the operation called; this does not imply the status of the command from the Server, only whether the operation was succesfully invoked.
     * @return True on succesful invocation.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Text output from the Server process upon invocation of the command.
     * @return All output text from the server.
     */
    public String getOutput() {
        return output;
    }

    /**
     * Server prompt after executing the command
     * @return Prompt value as a string (minus any cursor).
     */
    public String getPrompt() {
        return prompt;
    }

    private final boolean success;
    private final String  output;
    private final String  prompt;
}
