package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
//import java.util.ArrayList;
import java.util.Map;
// GraalVM specific import
import org.graalvm.collections.EconomicMap;

public class ConfigExample
{
    // GraalVM collections
    // static ArrayList<EconomicMap<String, String>> employeeData;

    static List<Map<String, String>> employeeData;

    static {
        try {
            System.out.println("Parsing employee file.");
            long start = System.currentTimeMillis();
            ObjectMapper mapper = new ObjectMapper();
            employeeData = mapper.readValue(ConfigExample.class.getResource("/account-list.json"), new TypeReference<>() {});
           
            // if using ArrayList
            //employeeData.trimToSize();

            System.out.println("Employee file parsed in: " + (System.currentTimeMillis() - start) + " ms.");
            System.out.println("Number of records: " + employeeData.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void storeEmployeeData(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(filePath), employeeData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello, world! Our fake employee list is " + employeeData.size() + " long!");
        if (employeeData.size() > 50) {
            System.out.println("Best fake employee ID: " + employeeData.get(50).get("_id") + ".");
        } else {
            System.out.println("We don't have a best fake employee!");
        }
        // storeEmployeeData("data.json");
    }
}
