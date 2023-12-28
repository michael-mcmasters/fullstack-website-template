package org.mcmasters.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.mcmasters.util.Log;
import org.mcmasters.model.Config;

import java.io.File;

public class ConfigService {

    public static Config config;


    public ConfigService() {
        try {
            Log.info("Initializing environment config for environment:" + System.getenv("env"));
            if (config != null) {
                Log.info("Using cached environment config for environment: " + System.getenv("env"));
                return;
            }

            File envFile = getEnvironmentFile();
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(envFile, Config.class);

            Log.info("Completed initializing new environment config");
        } catch (Exception e) {
            Log.error("Exception while saving to DynamoDB.", e);
            throw new RuntimeException("Failed to load environment config");
        }
    }

    private File getEnvironmentFile() {
        switch (System.getenv("env")) {
            case ("local"):
                Log.info("Running local configuration");
                return new File("./local.yml");
            case ("dev"):
                Log.info("Running dev configuration");
                return new File("./dev.yml");
            case ("test"):
                Log.info("Running test configuration");
                return new File("./test.yml");
            default:
                Log.error("Unable to find environment configuration for " + System.getenv("env") + ". Defaulting to local config");
                return new File("./local.yml");
        }
    }

}