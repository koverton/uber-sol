package com.solacesystems.ubersol.interactive;

import org.junit.Test;

import static org.junit.Assert.*;

public class CmdParserTest {
    @Test
    public void parseConnectTest() {
        CmdParser parser = new CmdParser();

        Cmd cmd = parser.parse("demotr   +     10.9.8.7       admin");
        assertEquals(OP.CLIConnect, cmd.getOp());
        ConnectCmd connCmd = (ConnectCmd) cmd;
        assertNotNull(connCmd);
        assertEquals("demotr", connCmd.getName());
        assertEquals("10.9.8.7", connCmd.getIpAndPort());
        assertEquals("admin", connCmd.getUser());
    }

    @Test
    public void parseSftpConnectTest() {
        CmdParser parser = new CmdParser();

        Cmd cmd = parser.parse("lftp * localhost:2222 ftpuser");
        assertEquals(OP.SFTPConnect, cmd.getOp());
        assertTrue( cmd instanceof ConnectCmd );
        ConnectCmd connCmd = (ConnectCmd) cmd;
        assertEquals("lftp", connCmd.getName());
        assertEquals("localhost:2222", connCmd.getIpAndPort());
        assertEquals("ftpuser", connCmd.getUser());
    }

    @Test
    public void badConnectTest() {
        CmdParser parser = new CmdParser();

        Cmd cmd = parser.parse("demotr   +     ");
        assertEquals(OP.Error, cmd.getOp());

        cmd = parser.parse("demotr   +     10.9.8.7 ");
        assertEquals(OP.Error, cmd.getOp());
    }

    @Test
    public void parseExitTest() {
        CmdParser parser = new CmdParser();

        Cmd cmd = parser.parse("  exit      ");
        assertEquals(OP.Exit, cmd.getOp());

        cmd = parser.parse("  exit  nobody cares whatever else comes after exit    ");
        assertEquals(OP.Exit, cmd.getOp());

        cmd = parser.parse("  exit  + looks like connect ");
        assertEquals(OP.Exit, cmd.getOp());
    }

    @Test
    public void parseRunScriptTest() {
        CmdParser parser = new CmdParser();

        Cmd cmd = parser.parse("foo ! src/test/resources/showversion.cli");
        assertEquals(OP.RunCLIScript, cmd.getOp());
        assertTrue( cmd instanceof RunScriptCmd );

        RunScriptCmd script = (RunScriptCmd) cmd;
        assertEquals( "src/test/resources/showversion.cli", script.getScriptName() );
    }

    @Test
    public void parseCliCmdTest() {
        CmdParser parser = new CmdParser();

        Cmd cmd = parser.parse("foo show version");
        assertEquals(OP.ExecCLI, cmd.getOp());
        assertTrue( cmd instanceof ExecCmd );

        ExecCmd exec = (ExecCmd) cmd;
        assertEquals("show version", exec.getCmd());
    }

    @Test
    public void parseSftpGetTest() {
        CmdParser parser = new CmdParser();

        Cmd cmd = parser.parse("dev* < logs/command*.log .");
        assertEquals(OP.SftpGet, cmd.getOp());
        assertTrue( cmd instanceof SftpCmd );

        SftpCmd sftp = (SftpCmd) cmd;
        assertEquals("logs/command*.log", sftp.getArg1());
        assertEquals(".", sftp.getArg2());
    }

    @Test
    public void parseSftpPutTest() {
        CmdParser parser = new CmdParser();

        Cmd cmd = parser.parse("dev* > cliscripts/ default-create.cli");
        assertEquals(OP.SftpPut, cmd.getOp());
        assertTrue( cmd instanceof SftpCmd );

        SftpCmd sftp = (SftpCmd) cmd;
        assertEquals("cliscripts/", sftp.getArg1());
        assertEquals("default-create.cli", sftp.getArg2());
    }
}
