package com.solacesystems.ubersol.interactive;

class SftpCmd extends Cmd {
    private SftpCmd(OP op, String name, String arg1, String arg2) {
        super(op, name);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    static public SftpCmd GET(String name, String remotePath, String localPath) {
        return new SftpCmd(OP.SftpGet, name, remotePath, localPath);
    }

    static public SftpCmd PUT(String name, String remotePath, String localPath) {
        return new SftpCmd(OP.SftpPut, name, remotePath, localPath);
    }

    static public SftpCmd SHOW(String name, String remotePaths) {
        return new SftpCmd(OP.SftpShow, name, remotePaths, null);
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    private final String arg1;
    private final String arg2;
}
