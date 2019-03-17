package com.solacesystems.ubersol.interactive;

public class ContextCmd extends Cmd {
    public ContextCmd(String connName) {
        super(OP.ShowContext, connName);
    }
}
