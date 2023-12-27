package org.mcmasters.service;

import org.mcmasters.util.Log;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;

public class RequestService {

    private DynamoDbService dynamoDbService;

    private final String dynamoDbTable;

    private int tempCounter = 0;


    public RequestService() {
        this.dynamoDbService = new DynamoDbService();
        this.dynamoDbTable = ConfigService.config.dynamodbTable;
    }

    public String process(String request) {
        try {
            Log.info("PlaceholderService is processing request");

            String key = "TestTableHashKey";
            String value = String.valueOf(tempCounter++);

            HashMap<String, AttributeValue> itemValues = new HashMap<>();

            // TestTableHashKey and its value
            itemValues.put(
                    key,
                    AttributeValue.builder()
                    .s(value)
                    .build()
            );

            // personName and its value
            itemValues.put(
                    "personName",
                    AttributeValue.builder()
                    .s("BillyBob")
                    .build()
            );

            dynamoDbService.save(dynamoDbTable, itemValues);

            Log.info("PlaceholderService completed processing request");
            return "Success!";
        } catch (Exception e) {
            Log.error("Exception in PlaceholderService while processing request", e);
            throw e;
        }
    }

}
