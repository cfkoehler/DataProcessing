package com.DpServer.util;

public final class Version {
    private String version = "missing version";
    private String timestamp = "missing timestamp";

    /**
     * Build a version object DO NOT call a logger from here
     */
    public Version() {
        // TODO: Set this to read from git properties file
        version = "1.0.0-SNAPSHOT";
        timestamp = "CURRENT!";
    }

    @Override
    public String toString() {
        return version + " " + timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static void main(String[] args) {
        System.out.println("Emissary version " + new emissary.util.Version());
        System.out.println("DpVersion version " + new Version());
    }
}