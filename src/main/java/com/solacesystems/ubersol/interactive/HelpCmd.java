package com.solacesystems.ubersol.interactive;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

class HelpCmd extends Cmd {
    public HelpCmd(String name, String resourceName) {
        super(OP.Help, name);
        this.message = readStringResource(resourceName);
    }

    public String getMessage() {
        return message;
    }


    private String readStringResource(String resourceName) {
        InputStream stream = getClass().getResourceAsStream(resourceName);
        StringBuilder s = new StringBuilder();
        try {
            char[] buf = new char[2048];
            Reader r = new InputStreamReader(stream, "UTF-8");
            while (true) {
                int n = r.read(buf);
                if (n < 0)
                    break;
                s.append(buf, 0, n);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            // TODO! LOGGING!!!
        }
        return s.toString();
    }

    private final String message;
}
