package org.mcmasters.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mcmasters.model.AddItemRequestBody;
import org.mcmasters.model.AddItemResponseBody;
import org.mcmasters.model.GetItemResponseBody;
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

    public AddItemResponseBody addItem(APIGatewayProxyRequestEvent request, String apiVersion) throws IOException {
        try {
            Log.info("CrudService is processing /add-item endpoint using body: " + request.getBody());

            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            AddItemRequestBody addItemRequestBody = mapper.readValue(request.getBody(), AddItemRequestBody.class);

            HashMap<String, AttributeValue> itemValues = new HashMap<>();

            // TestTableHashKey column and its value
            itemValues.put(
                    key,
                    AttributeValue.builder()
                            .s(addItemRequestBody.key)
                            .build()
            );

            // personName column and its value
            itemValues.put(
                    "personName",
                    AttributeValue.builder()
                            .s(addItemRequestBody.personName)
                            .build()
            );

            dynamoDbService.save(dynamoDbTable, itemValues);

            AddItemResponseBody addItemResponseBody = new AddItemResponseBody();
            addItemResponseBody.setMessage("Successfully added item to DynamoDb!");
            addItemResponseBody.setApiVersion(apiVersion);

            Log.info("CrudService completed processing /add-item endpoint");

            return addItemResponseBody;
        } catch (Exception e) {
            Log.error("Exception in CrudService while processing /add-item endpoint", e);
            throw e;
        }
    }

    public GetItemResponseBody getItem(APIGatewayProxyRequestEvent request, String apiVersion) {
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
            GetItemResponseBody getItemResponseBody = new GetItemResponseBody();
            getItemResponseBody.setKey(result.get(key).s());
            getItemResponseBody.setPersonName(result.get("personName").s());
            getItemResponseBody.setApiVersion(apiVersion);

            Log.info("CrudService completed processing /get-item endpoint");
            return getItemResponseBody;

        } catch (Exception e) {
            Log.error("Exception in CrudService while processing /get-item endpoint", e);
            throw e;
        }
    }

}
