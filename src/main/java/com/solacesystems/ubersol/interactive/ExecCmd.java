package com.solacesystems.ubersol.interactive;

class ExecCmd extends Cmd {
    public ExecCmd(String name, String cmd) {
        super(OP.ExecCLI, name);
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    private final String cmd;
}
