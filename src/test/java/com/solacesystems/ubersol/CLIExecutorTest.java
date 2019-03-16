package com.solacesystems.ubersol;

import com.solacesystems.ubersol.CLIExecutor;
import com.solacesystems.ubersol.CLIExecutorImpl;
import com.solacesystems.ubersol.Response;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CLIExecutorTest {
    private final static String rtrname = "d86acc5f20a6";


    CLIExecutor getCLI() {
        return new CLIExecutorImpl("test");
    }

    private void delimit(String message) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(message);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("");
    }

    @Test
    public void ConnectTest() {
        String hostname = "localhost";
        CLIExecutor cliExecutor = getCLI();
        System.out.println("Connecting ...");
        Response r = cliExecutor.connect(hostname, "localhost:2222", "admin", "admin");
        assertEquals(rtrname+">", r.getPrompt());

        Map<String,Response> results = cliExecutor.execute(hostname, "enable");
        for(Map.Entry<String,Response> entry : results.entrySet()) {
            assertEquals(hostname, entry.getKey());
            assertEquals(rtrname+"#", entry.getValue().getPrompt());
        }
        results = cliExecutor.execute(hostname, "configure");
        for(Map.Entry<String,Response> entry : results.entrySet())
            assertEquals(hostname, entry.getKey());
    }

    @Test
    public void showHostnameTest() {
        String hostname = "localhost";
        CLIExecutor cliExecutor = getCLI();
        System.out.println("Connecting ...");
        Response r = cliExecutor.connect(hostname, "localhost:2222", "admin", "admin");
        delimit(r.getOutput());

        System.out.println("Executing 'show hostname' ...");

        Map<String,Response> results = cliExecutor.execute(hostname, "show hostname");
        for(Map.Entry<String,Response> entry : results.entrySet())
            delimit(entry.getValue().getOutput());
    }


    @Test
    public void executeCommentTest() {
        String hostname = "localhost";
        CLIExecutor cliExecutor = getCLI();
        System.out.println("Connecting ...");
        Response r = cliExecutor.connect(hostname, "localhost:2222", "admin", "admin");
        delimit(r.getOutput());

        System.out.println("Executing a ! Commented line");
        Map<String,Response> results = cliExecutor.execute(hostname, "! this is a comment");
        for(Map.Entry<String,Response> entry : results.entrySet())
            delimit(entry.getValue().getOutput());
    }

    @Ignore
    public void executeScriptTest() {
        String hostname = "localhost";
        CLIExecutor cliExecutor = getCLI();
        System.out.println("Connecting ...");
        Response r = cliExecutor.connect(hostname, "localhost:2222", "admin", "admin");
        delimit(r.getOutput());

        String scriptname = "resources/scripts/script1.cli";
        System.out.println("Executing script: " + scriptname);

        Map<String,Response> results = cliExecutor.executeScript(hostname, scriptname);
        for(Map.Entry<String,Response> entry : results.entrySet())
            delimit(entry.getValue().getOutput());
    }
}
