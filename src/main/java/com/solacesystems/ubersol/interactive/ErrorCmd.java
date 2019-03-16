package com.solacesystems.ubersol.interactive;

class ErrorCmd extends Cmd {
    public ErrorCmd(String name, String message) {
        super(OP.Error, name);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;
}
