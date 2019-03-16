package com.solacesystems.ubersol;

import com.jcraft.jsch.*;

import java.util.*;

/**
 * Implementation of the {@link com.solacesystems.ubersol.SftpExecutor} interface.
 *
 * @see com.solacesystems.ubersol.SftpExecutor
 * @see com.solacesystems.ubersol.Response
 */
public class SftpExecutorImpl implements SftpExecutor {
    private class SessionState {
        Session session;
        Channel channel;
        ChannelSftp sftpChan;
    }

    public SftpExecutorImpl() {
        jsch = new JSch();
        sessionMap = new HashMap<>();
    }

    @Override
    public Response connect(String connectionName, String ipAndPort, String user, String password) {
        if (!sessionMap.containsKey(connectionName)) {
            int port = Helper.getPort(ipAndPort);
            String host = Helper.getHost(ipAndPort);
            SessionState state = new SessionState();
            try {
                state.session = jsch.getSession(user, host, port);
                // Turn off all host-checks for now
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                state.session.setConfig(config);
                state.session.setPassword(password);
                state.session.connect(10000);
                state.channel = state.session.openChannel("sftp");
                state.channel.connect(3000);
                state.sftpChan=(ChannelSftp)state.channel;
                sessionMap.put(connectionName, state);
                return new Response(true, "success", "sftp");
            }
            catch(JSchException jex) {
                String msg = "SFTP Connect failed: " + jex.getMessage();
                return new Response(false, msg, "sftp");
            }
            catch(Throwable t) {
                return new Response(false, "Unknown exception thrown: "+t.getMessage()+"\n", "sftp");
            }
        }
        return new Response(false, "Unknown state: connection probably failed\n", "sftp");
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
        SessionState st = sessionMap.get(connectionName);
        st.channel.disconnect();
        st.session.disconnect();
        sessionMap.remove(connectionName);
        return new Response(true, "disconnected\n", "sftp");
    }

    @Override
    public Map<String, Response> get(String connExpr, String remotePath, String localPath) {
        Map<String, Response> result = new HashMap<>();
        List<String> matches = Helper.glob(sessionMap.keySet(), connExpr);
        for(String connectionName : matches) {
            result.put(connectionName, getOne(connectionName, remotePath, localPath));
        }
        if (result.size() < 1)
            result.put(connExpr, new Response(false, "No sessions matched '"+connExpr+"'", connExpr));
        return result;
    }

    public Response getOne(String connectionName, String remotePath, String localPath) {
        boolean status;
        String msg = "";
        SessionState state = sessionMap.get(connectionName);
        try {
            state.sftpChan.get(remotePath, localPath);
            status = true;
        }
        catch(SftpException ex) {
            status = false;
            msg = ex.getMessage();
        }
        return new Response(status, msg, "sftp");
    }

    @Override
    public Map<String, Response> put(String connExpr, String remotePath, String localPath) {
        Map<String, Response> result = new HashMap<>();
        List<String> matches = Helper.glob(sessionMap.keySet(), connExpr);
        for(String connectionName : matches) {
            result.put(connectionName, putOne(connectionName, remotePath, localPath));
        }
        if (result.size() < 1)
            result.put(connExpr, new Response(false, "No sessions matched '"+connExpr+"'", connExpr));
        return result;
    }

    public Response putOne(String connectionName, String remotePath, String localPath) {
        boolean status;
        String msg = "";
        SessionState state = sessionMap.get(connectionName);
        try {
            state.sftpChan.put(remotePath, localPath);
            status = true;
        }
        catch(SftpException ex) {
            status = false;
            msg = ex.getMessage();
        }
        return new Response(status, msg, "sftp");
    }

    @Override
    public Map<String, Response> dir(String connExpr, String remotePath) {
        Map<String, Response> result = new HashMap<>();
        List<String> matches = Helper.glob(sessionMap.keySet(), connExpr);
        for(String connectionName : matches) {
            result.put(connectionName, dirOne(connectionName, remotePath));
        }
        if (result.size() < 1)
            result.put(connExpr, new Response(false, "No sessions matched '"+connExpr+"'", connExpr));
        return result;
    }


    public Response dirOne(String connectionName, String path) {
        boolean status;
        StringBuilder sb = new StringBuilder();
        SessionState state = sessionMap.get(connectionName);
        try{
            for(Object o : state.sftpChan.ls(path)) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry)o;
                if (entry != null) {
                    sb.append(entry.getFilename()).append("\n");
                }
            }
            status = true;
        }
        catch(SftpException e){
            sb.append("ERROR: ").append(e.getMessage());
            status = false;
        }
        return new Response(status, sb.toString(), "sftp");
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

    private final JSch jsch;
    private final Map<String, SessionState> sessionMap;
}
