package org.mcmasters;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;

public class PlaceholderService {

    private DynamoDbService dynamoDbService;

    private int tempCounter = 0;


    public PlaceholderService() {
        this.dynamoDbService = new DynamoDbService();
    }

    public void process(String request) {
        try {
            Log.info("PlaceholderService is processing request");

            String tableName = ConfigProcessor.config.dynamodbTable;
            String key = "TestTableHashKey";
            String value = String.valueOf(tempCounter++);

            HashMap<String, AttributeValue> itemValues = new HashMap<>();

            // Each itemValues.put() adds a new column to the same row.
            // Key is the column name.
            // Value is the value

            // TestTableHashKey and its value
            itemValues.put(key,
                    AttributeValue.builder()
                    .s(value)
                    .build()
            );

            // personName and its value
            itemValues.put("personName",
                    AttributeValue.builder()
                    .s("BillyBob")
                    .build()
            );

            dynamoDbService.save(tableName, itemValues);

            Log.info("PlaceholderService completed processing request");
        } catch (Exception e) {
            Log.error("Exception in PlaceholderService while processing request", e);
        }
    }

}
