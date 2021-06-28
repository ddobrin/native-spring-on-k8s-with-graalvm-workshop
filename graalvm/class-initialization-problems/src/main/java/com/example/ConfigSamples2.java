package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hidden class init problems
 * 1. The file could be very large  -- think large org loading a serious set of reference data
 *                                  -- use more economical data structures, say trim() the ArrayList
 * 2. Code compatibility - initializing run-time classes unintentionally
 *
 * Recommendations  -- devs - start with runtime, let the system decide
 *                  -- optimize only afterwards
 *                  -- building FW? - we need to understand
 *
 *                  -- split the class into build-time and runtime
 *                  -- anti-pattern in Java - avoid complex initializers
 *
 * Singletons   -- create them as one in its own class --> more verbose --> clear delineation
 */
public class ConfigSamples2 {
    static int threadsStatic;
    static int loadStatic;

    static JsonNode root;
    static ObjectMapper omap;
    static {
        try {
            String json = readInputStream(ClassLoader.getSystemResourceAsStream("config.json"));

            omap = new ObjectMapper();
            root = omap.readTree(json);
            threadsStatic = root.path("config.threads").asInt();
            loadStatic = root.path("config.load").asInt();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConfigSamples2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final int count;
    public final int load;

    public static void main(String[] args) throws InterruptedException, NumberFormatException, JsonProcessingException {
        ConfigSamples2 inst = new ConfigSamples2(
                args.length == 0 ? threadsStatic : Integer.parseInt(args[0]),
                args.length > 1 ? Integer.parseInt(args[1]) : loadStatic
        );

        System.out.println("config.threads " + inst.count);
        System.out.println("config.load " + inst.load);

//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.writeValue(new File("output.json"), root);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            omap.writeValue(new File("output.json"), root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    ConfigSamples2(int count, int load) {
        this.count = count;
        this.load = load;
    }

    private static String readInputStream(InputStream is) {
        StringBuilder out = new StringBuilder();
        try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }

        } catch (IOException e) {
            Logger.getLogger(ConfigSamples2.class.getName()).log(Level.SEVERE, null, e);
        }
        return out.toString();
    }
}
