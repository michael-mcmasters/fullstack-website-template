package org.mcmasters.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mcmasters.model.AddRequestBody;
import org.mcmasters.util.Log;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
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

    public String getItem(APIGatewayProxyRequestEvent request) {
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

    public String addItem(APIGatewayProxyRequestEvent request) throws IOException {
        try {
            Log.info("CrudService is processing /add-item endpoint");

            String[] splitPath = request.getPath().split("/");
            String param = splitPath[splitPath.length - 1];
            Log.info("Endpoint param is " + param);

            System.out.println("body is " + request.getBody());
//            request.getBody().get

            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            AddRequestBody addRequestBody = mapper.readValue(request.getBody(), AddRequestBody.class);
            System.out.println("addRequest.key: " + addRequestBody.key);
            System.out.println("addRequest.personName: " + addRequestBody.personName);

            HashMap<String, AttributeValue> itemValues = new HashMap<>();

            // TestTableHashKey column and its value
            itemValues.put(
                    key,
                    AttributeValue.builder()
                        .s(addRequestBody.key)
                        .build()
            );

            // personName column and its value
            itemValues.put(
                    "personName",
                    AttributeValue.builder()
                        .s(addRequestBody.personName)
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
