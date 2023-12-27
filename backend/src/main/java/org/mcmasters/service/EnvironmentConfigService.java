package org.mcmasters.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.mcmasters.util.Log;
import org.mcmasters.model.Config;

import java.io.File;

public class EnvironmentConfigService {

    public static Config config;


    public void setupConfig() {
        try {
            Log.info("Initializing environment config");
            if (config != null) {
                Log.info("Using cached environment config for environment: " + System.getenv("env"));
                return;
            }

            File envFile = getEnvFile();

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(envFile, Config.class);

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