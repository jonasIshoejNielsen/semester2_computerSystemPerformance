package org.simple.software.meaurements;

public class Config {
    public static boolean writeCleaningTags  = true;
    public static boolean writeWordCount     = true;
    public static boolean writeSerializing   = true;
    public static boolean writeResponseTime  = true;
    public static boolean writeTimeInServer  = true;
    public static boolean writeTimeInQueue   = true;

    public static void setAllToFalse() {
        writeCleaningTags  = false;
        writeWordCount     = false;
        writeSerializing   = false;
        writeResponseTime  = false;
        writeTimeInServer  = false;
    }

}
