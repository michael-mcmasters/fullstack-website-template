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


    public CrudService() {
        this.dynamoDbService = new DynamoDbService();
        this.dynamoDbTable = ConfigService.config.dynamodbTable;
        this.key = ConfigService.config.dynamodbKey;
    }

    public String get(APIGatewayProxyRequestEvent request) {
        try {
            Log.info("CrudService is processing /get-item endpoint");

            String[] splitPath = request.getPath().split("/");
            String param = splitPath[splitPath.length - 1];
            Log.info("Endpoint param is " + param);

            HashMap<String, AttributeValue> itemValues = new HashMap<>();

            // TestTableHashKey column and its value
            itemValues.put(
                    key,
                    AttributeValue.builder()
                        .s(param)
                        .build()
            );

            Map<String, AttributeValue> result = dynamoDbService.read(dynamoDbTable, itemValues);

            Log.info("CrudService completed processing /get-item endpoint");
            return result.toString();
        } catch (Exception e) {
            Log.error("Exception in CrudService while processing /get-item endpoint", e);
            throw e;
        }
    }

    public String add(APIGatewayProxyRequestEvent request) {
        try {
            Log.info("CrudService is processing /add-item endpoint");

            String[] splitPath = request.getPath().split("/");
            String param = splitPath[splitPath.length - 1];
            Log.info("Endpoint param is " + param);

            System.out.println("body is " + request.getBody());

            HashMap<String, AttributeValue> itemValues = new HashMap<>();

            // TestTableHashKey column and its value
            itemValues.put(
                    key,
                    AttributeValue.builder()
                        .s(param)
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

            Log.info("CrudService completed processing /add-item endpoint");
            return "Success!";
        } catch (Exception e) {
            Log.error("Exception in CrudService while processing /add-item endpoint", e);
            throw e;
        }
    }

}
