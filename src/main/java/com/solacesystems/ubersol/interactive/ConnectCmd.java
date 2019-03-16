package com.solacesystems.ubersol.interactive;

class ConnectCmd extends Cmd {
    public ConnectCmd(String name, String ipAndPort, String user, boolean sftpConnection) {
        super((sftpConnection ? OP.SFTPConnect : OP.CLIConnect), name);
        this.ipAndPort = ipAndPort;
        this.user = user;
    }

    public String getIpAndPort() {
        return ipAndPort;
    }

    public String getUser() {
        return user;
    }

    private final String ipAndPort;
    private final String user;
}
