package com.solacesystems.ubersol;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SftpExecutorTest {
    @Ignore
    public void downloadTest() {
        SftpExecutor executor = new SftpExecutorImpl();
        Response resp = executor.connect("docker", "localhost:2222", "sftp", "sftp");
        assertTrue(resp.isSuccess());

        Map<String,Response> results = executor.get("*", "logs/recoverFailedDisk.log", ".");
        assertEquals(1, results.size());
    }
    @Ignore
    public void uploadTest() {
        SftpExecutor executor = new SftpExecutorImpl();
        Response resp = executor.connect("docker", "localhost:2222", "sftp", "sftp");
        assertTrue(resp.isSuccess());

        Map<String,Response> results = executor.put("*", "recoverFailedDisk.log", "logs/recoverFailedDisk.log.uploaded");
        assertEquals(1, results.size());
    }
    @Ignore
    public void dirTest() {
        SftpExecutor executor = new SftpExecutorImpl();
        Response resp = executor.connect("docker", "localhost:2222", "sftp", "sftp");
        assertTrue(resp.isSuccess());

        Map<String,Response> results = executor.dir("*", "logs/*.log");
        assertEquals(1, results.size());
        for(Map.Entry<String,Response> entry : results.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().getOutput());
        }
    }
}
