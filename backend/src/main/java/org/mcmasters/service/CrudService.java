package org.mcmasters.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.mcmasters.util.Log;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class CrudService {

    private final DynamoDbService dynamoDbService;

    private final String dynamoDbTable;

    private final String key;

    private int tempCounter = 0;


    public CrudService() {
        this.dynamoDbService = new DynamoDbService();
        this.dynamoDbTable = ConfigService.config.dynamodbTable;
        this.key = ConfigService.config.dynamodbKey;
    }

    public String get(APIGatewayProxyRequestEvent request) {
        try {
            Log.info("CrudService is processing /get endpoint");

            HashMap<String, AttributeValue> itemValues = new HashMap<>();

            // TestTableHashKey column and its value
            itemValues.put(
                    key,
                    AttributeValue.builder()
                        .s("0")
                        .build()
            );

            Map<String, AttributeValue> result = dynamoDbService.read(dynamoDbTable, itemValues);

            Log.info("CrudService completed processing /get endpoint");
            return result.toString();
        } catch (Exception e) {
            Log.error("Exception in CrudService while processing /get endpoint", e);
            throw e;
        }
    }

    public String add(APIGatewayProxyRequestEvent request) {
        try {
            Log.info("CrudService is processing /add endpoint");

            String value = String.valueOf(tempCounter++);

            HashMap<String, AttributeValue> itemValues = new HashMap<>();

            // TestTableHashKey column and its value
            itemValues.put(
                    key,
                    AttributeValue.builder()
                        .s(value)
                        .build()
            );

            // personName column and its value
            itemValues.put(
                    "personName",
                    AttributeValue.builder()
                        .s("BillyBob")
                        .build()
            );

            dynamoDbService.save(dynamoDbTable, itemValues);

            Log.info("CrudService completed processing /add endpoint");
            return "Success!";
        } catch (Exception e) {
            Log.error("Exception in CrudService while processing /add endpoint", e);
            throw e;
        }
    }

}
