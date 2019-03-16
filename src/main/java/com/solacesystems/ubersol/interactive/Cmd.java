package com.solacesystems.ubersol.interactive;

class Cmd {

    public Cmd(OP op, String name) {
        this.op = op;
        this.name = name;
    }

    public OP getOp() {
        return op;
    }

    public String getName() {
        return name;
    }

    private final OP op;
    private final String name;
}
