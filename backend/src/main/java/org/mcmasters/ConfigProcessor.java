package org.mcmasters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class ConfigProcessor {

    public static Config config;


    public void init() {
        try {
            Log.info("Initializing environment config");

            File envFile = getEnvFile();

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(envFile, Config.class);
            System.out.println("YAML region is " + config.region);
            System.out.println("YAML table is " + config.dynamodbTable);

            Log.info("Completed initializing environment config");
        } catch (Exception e) {
            Log.error("Exception while saving to DynamoDB.", e);
            throw new RuntimeException("Failed to load environment config");
        }
    }

    private File getEnvFile() {
        switch (System.getenv("env")) {
            case ("local"):
                Log.info("Running local configuration");
                return new File("./local.yml");
            case ("dev"):
                Log.info("Running dev configuration");
                return new File("./local.yml");
            case ("test"):
                Log.info("Running test configuration");
                return new File("./local.yml");
            default:
                Log.error("Unable to find environment configuration for " + System.getenv("env") + ". Defaulting to local config");
                return new File("./local.yml");
        }
    }


//    public static void init() throws IOException {
//        Log.info("Initializing environment config");
//
//        File file = new File("./local.yml");
//
//        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//        Config config = mapper.readValue(file, Config.class);
//        System.out.println("YAML REGION IS " + config.region);
//
//        switch (System.getenv("env")) {
//            case ("local"):
//                Log.info("Running local configuration");
//                REGION = "us-east-1";
//                DYNAMODB_TABLE = "website-and-infra-2-dynamodb-dev";
//                break;
//            case ("dev"):
//                Log.info("Running dev configuration");
//                break;
//            case ("test"):
//                Log.info("Running test configuration");
//                break;
//            default:
////                Log.error("Unable to find environment configuration for " + System.getenv("env") + ". Defaulting to local config");
//                Log.error("Unable to find environment configuration for " + "AAA" + ". Defaulting to local config");
//                REGION = "us-east-1";
//                DYNAMODB_TABLE = "website-and-infra-2-dynamodb-dev";
//                break;
//        }
//    }
}