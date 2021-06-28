package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigSamples {
    static int threadsStatic;
    static int loadStatic;
    static {
        try {
            String json = readInputStream(ClassLoader.getSystemResourceAsStream("config.json"));
            ObjectMapper omap = new ObjectMapper();
            JsonNode root = omap.readTree(json);
            threadsStatic = root.path("config.threads").asInt();
            loadStatic = root.path("config.load").asInt();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConfigSamples.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final int count;
    public final int load;

    public static void main(String[] args) throws InterruptedException, NumberFormatException, JsonProcessingException {
        ConfigSamples inst = new ConfigSamples(
                args.length == 0 ? threadsStatic : Integer.parseInt(args[0]),
                args.length > 1 ? Integer.parseInt(args[1]) : loadStatic
        );

        System.out.println("config.threads " + inst.count);
        System.out.println("config.load " + inst.load);


//        try (ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream("bear-id"))) {
//            oss.writeObject(ids);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    ConfigSamples(int count, int load) {
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
            Logger.getLogger(ConfigSamples.class.getName()).log(Level.SEVERE, null, e);
        }
        return out.toString();
    }
}
