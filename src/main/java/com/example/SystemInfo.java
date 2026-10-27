package com.example;

/**
 * Helper class that reports version information about the running program.
 *
 * <p>All methods are static, so you call them on the class itself
 * (for example {@code SystemInfo.javaVersion()}) without creating an object.</p>
 */
public class SystemInfo {

    /**
     * Gets the version of Java the program is running on.
     *
     * @return the Java version, for example "26.0.2"
     */
    public static String javaVersion() {
        // "java.version" is a standard system property that the JVM always sets
        return System.getProperty("java.version");
    }

    /**
     * Gets the version of JavaFX the program is using.
     *
     * @return the JavaFX version, or null if JavaFX has not started yet
     */
    public static String javafxVersion() {
        // JavaFX sets the "javafx.version" property once its toolkit starts up
        return System.getProperty("javafx.version");
    }

}
