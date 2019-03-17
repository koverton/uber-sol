package com.solacesystems.ubersol;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.*;

/**
 * Implementation of the {@link com.solacesystems.ubersol.CLIExecutor} interface.
 *
 * @see com.solacesystems.ubersol.CLIExecutor
 * @see com.solacesystems.ubersol.Response
 */
public class CLIExecutorImpl implements CLIExecutor {
    private class SessionState {
        Session session;
        Channel channel;
        InputStreamReader reader;
        OutputStreamWriter writer;
        String lastPrompt;
    }

    public CLIExecutorImpl(final String name) {
        prompt = name;
        jsch = new JSch();
        sessionMap = new HashMap<>();
    }

    @Override
    public Response connect(final String connectionName, final String ipAndPort, final String user, final String password) {
        if (!sessionMap.containsKey(connectionName)) {
            int port = Helper.getPort(ipAndPort);
            String host = Helper.getHost(ipAndPort);
            SessionState state = new SessionState();
            state.lastPrompt = prompt;
            try {
                state.session = jsch.getSession(user, host, port);
                // Turn off all host-checks for now
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                state.session.setConfig(config);
                state.session.setPassword(password);
                state.session.connect(10000);
                state.channel = state.session.openChannel("shell");
                state.reader = new InputStreamReader(state.channel.getInputStream());
                state.writer = new OutputStreamWriter(state.channel.getOutputStream());
                state.channel.connect(3000);
                sessionMap.put(connectionName, state);
                String[] output = Helper.readResponse(state.reader);
                state.lastPrompt = output[0];
                // Turn off paging always(!)
                Response resp = executeOne(connectionName, "no paging");
                return new Response(true, output[1]+"\n", resp.getPrompt());
            }
            catch(JSchException jex) {
                String msg = "CLIConnect failed: " + jex.getMessage();
                return new Response(false, msg, state.lastPrompt);
            }
            catch(IOException ioex) {
                return new Response(false, ioex.getMessage()+"\n", state.lastPrompt);
            }
            catch(Throwable t) {
                return new Response(false, "Unknown exception thrown: "+t.getMessage()+"\n", state.lastPrompt);
            }
        }
        return new Response(false, "Unknown state: connection probably failed\n", prompt);
    }

    @Override
    public Map<String, Response> disconnect(String connExpr) {
        Map<String, Response> result = new HashMap<>();
        List<String> matches = Helper.glob(sessionMap.keySet(), connExpr);
        for(String connectionName : matches) {
            result.put(connectionName, disconnectOne(connectionName));
        }
        return result;
    }

    private Response disconnectOne(String connectionName) {
        boolean status;
        SessionState st = sessionMap.get(connectionName);
        String msg = "";
        try {
            st.reader.close();
            st.writer.close();
            st.channel.disconnect();
            st.session.disconnect();
            msg = "Disconnected.";
            status = true;
        }
        catch(IOException e) {
            msg += e.getLocalizedMessage();
            status = false;
        }
        sessionMap.remove(connectionName);
        return new Response(status, msg+"\n", st.lastPrompt);
    }

    @Override
    public Map<String, Response> execute(String connExpr, String command) {
        Map<String, Response> result = new HashMap<>();
        List<String> matches = Helper.glob(sessionMap.keySet(), connExpr);
        for(String connectionName : matches) {
            Response response = executeOne(connectionName, command);
            if (Helper.requiresInput(response.getOutput())) {
                // Force 'y' response
                Response yresp = executeOne(connectionName, "y");
                // append this response to the last response
                String newOutput = response.getOutput() +yresp.getOutput();
                result.put(connectionName,
                        new Response(yresp.isSuccess(), newOutput, yresp.getPrompt()));
            }
            else {
                result.put(connectionName, response);
            }
        }
        if (result.size() < 1)
            result.put(connExpr, new Response(false, "No sessions matched '"+connExpr+"'", connExpr));
        return result;
    }

    private Response executeOne(String connectionName, String command) {
        SessionState st = sessionMap.get(connectionName);
        try {
            st.writer.write(command);
            st.writer.write("\n");
            st.writer.flush();
            String[] input = Helper.readResponse(st.reader);
            st.lastPrompt = input[0];
            return new Response(true, input[1]+"\n", st.lastPrompt);
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
            return new Response(false, ioex.getMessage()+"\n", st.lastPrompt);
        }
    }

    @Override
    public Map<String,Response> executeScript(String connExpr, String scriptFileName) {
        Map<String, Response> result = new HashMap<>();
        List<String> matches = Helper.glob(sessionMap.keySet(), connExpr);
        for(String connectionName : matches) {
            result.put(connectionName, executeScriptOne(connectionName, scriptFileName));
        }
        return result;
    }

    private Response executeScriptOne(String connectionName, String scriptFileName) {
        boolean status = true;
        StringBuilder response = null;
        BufferedReader br = null;
        String lastPrompt = prompt;

        try {
            response = new StringBuilder();
            br = new BufferedReader(new FileReader(new File(scriptFileName)));
            String cmd;
            while ((cmd = br.readLine()) != null) {
                cmd = cmd.trim();
                if (cmd.length() > 0) {
                    Response tmpResp = executeOne(connectionName, cmd);
                    lastPrompt = tmpResp.getPrompt();
                    if (!tmpResp.isSuccess())
                        status = false;
                    response.append(tmpResp.getOutput())
                            .append("\n");
                }
            }
        }
        catch(FileNotFoundException fex) {
            status = false;
            response.append("File Not Found: ")
                    .append(scriptFileName)
                    .append("\n");
        }
        catch(IOException ioex) {
            status = false;
            response.append("IO Exception reading file: ")
                    .append(scriptFileName)
                    .append("\n");
        }
        finally {
            try {
                if (br != null) br.close();
            } catch(IOException ioex) {
                ioex.printStackTrace();
            }
        }

        return new Response(status, response.toString(), lastPrompt);
    }


    @Override
    public List<String> listSessions() {
        List<String> result = new ArrayList<>();
        for(Map.Entry<String, SessionState> entry : sessionMap.entrySet()) {
            StringBuilder sbldr = new StringBuilder();
            sbldr.append(entry.getKey()).append("=>");
            Session session = entry.getValue().session;
            sbldr.append(session.getHost()).append(':').append(session.getPort());
            result.add(sbldr.toString());
        }
        return result;
    }

    @Override
    public Map<String, Response> listContext(String connExpr) {
        Map<String, Response> result = new HashMap<>();
        for(String connectionName : Helper.glob(sessionMap.keySet(), connExpr)) {
            SessionState st = sessionMap.get(connectionName);
            result.put(connectionName, new Response(true, st.lastPrompt, st.lastPrompt));
        }
        return result;
    }

    private final JSch jsch;
    private final Map<String, SessionState> sessionMap;
    private final String prompt;
}
