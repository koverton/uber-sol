package com.solacesystems.ubersol.interactive;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HelpCmdTest {

    @Test
    public void topTest() {
        HelpCmd cmd = new HelpCmd("help", HelpMsg.TOP_LEVEL);
        System.out.println(cmd.getMessage());
        assertTrue(cmd.getMessage().length() > 0);
    }

    @Test
    public void cliTest() {
        HelpCmd cmd = new HelpCmd("help", HelpMsg.CLIEXEC);
        System.out.println(cmd.getMessage());
        assertTrue(cmd.getMessage().length() > 0);
    }

    @Test
    public void connectTest() {
        HelpCmd cmd = new HelpCmd("help", HelpMsg.CONNECT);
        System.out.println(cmd.getMessage());
        assertTrue(cmd.getMessage().length() > 0);
    }

    @Test
    public void sftpTest() {
        HelpCmd cmd = new HelpCmd("help", HelpMsg.SFTPEXEC);
        System.out.println(cmd.getMessage());
        assertTrue(cmd.getMessage().length() > 0);
    }
}
