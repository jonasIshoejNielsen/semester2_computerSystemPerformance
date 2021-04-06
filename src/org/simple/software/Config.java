package org.simple.software;

public class Config {
    public static boolean writeCleaningTags  = true;
    public static boolean writeWordCount     = true;
    public static boolean writeSerializing   = true;
    public static boolean writeResponseTime  = true;
    public static boolean writeTimeInServer  = true;

    public static void setAllToFalse() {
        writeCleaningTags  = false;
        writeWordCount     = false;
        writeSerializing   = false;
        writeResponseTime  = false;
        writeTimeInServer  = false;
    }

}
