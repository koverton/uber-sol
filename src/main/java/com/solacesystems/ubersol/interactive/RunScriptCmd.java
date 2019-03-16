package com.solacesystems.ubersol.interactive;

class RunScriptCmd extends Cmd {
    public RunScriptCmd(String name, String scriptName) {
        super(OP.RunCLIScript, name);
        this.scriptName = scriptName;
    }

    public String getScriptName() {
        return scriptName;
    }

    private final String scriptName;
}
