package com.example;

public class SharedUtilsHolder {
    // Will always contain the same random seed at image runtime!
    private static final SimplePRNG randomNumberGenerator = new SimplePRNG(System.currentTimeMillis());

    // read a property
    // we don't know who could use this method in build-time initialization
    public static String readProperties() {
        return System.getProperty("user.home");
    }

    // return a random number
    // we don't know who could use this method in build-time initialization
    public static long getRandomNumber(){
        return randomNumberGenerator.nextRandom();
    }
}

