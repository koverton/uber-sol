package com.solacesystems.ubersol.interactive;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class HelperTest {
    private char[] specialChars = new char[] { '+', '-', '!', '<', '>' };

    @Test
    public void testSpecialChars() {
        List<String> result = Helper.splitRespectingSpecialChars("demotr   +     10.9.8.7       admin", specialChars);
        assertEquals(4, result.size());
        result = Helper.splitRespectingSpecialChars("demotr+10.9.8.7       admin", specialChars);
        assertEquals(4, result.size());
        result = Helper.splitRespectingSpecialChars("* show ip vrf msg-backbone", specialChars);
        assertEquals(5, result.size());
    }
}
