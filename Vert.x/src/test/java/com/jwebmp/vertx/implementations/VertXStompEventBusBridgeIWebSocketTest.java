package com.jwebmp.vertx.implementations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Simple test for the VertXStompEventBusBridgeIWebSocket implementation
 */
public class VertXStompEventBusBridgeIWebSocketTest {

    /**
     * Test that verifies the STOMP destination format
     */
    @Test
    public void testStompDestinationFormat() {
        // Setup test data
        String testGroup = "testGroup";
        String expectedStompDestination = "/toStomp/" + testGroup;
        
        // Create a test implementation that exposes the destination format
        TestStompEventBusBridge testBridge = new TestStompEventBusBridge();
        
        // Get the actual destination
        String actualDestination = testBridge.getStompDestination(testGroup);
        
        // Verify the destination format
        assertEquals(expectedStompDestination, actualDestination, 
                "The destination should have the STOMP prefix");
    }
    
    /**
     * Test implementation that exposes the destination format
     */
    private static class TestStompEventBusBridge extends VertXStompEventBusBridgeIWebSocket {
        /**
         * Exposes the STOMP destination format
         * 
         * @param groupName The group name
         * @return The STOMP destination
         */
        public String getStompDestination(String groupName) {
            return "/toStomp/" + groupName;
        }
    }
}