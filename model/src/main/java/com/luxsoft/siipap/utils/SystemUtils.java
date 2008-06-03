package com.luxsoft.siipap.utils;

public class SystemUtils {

	
	/**
     * Sleeps for the given <code>milliseconds</code> 
     * catching a potential InterruptedException.
     * 
     * @param milliseconds   the time to sleep in milliseconds 
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {}
    }
    
}
