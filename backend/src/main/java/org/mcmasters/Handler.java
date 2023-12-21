package org.mcmasters;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String version = "7";

    private PlaceholderService placeholderService;


    public Handler() {
        this.placeholderService = new PlaceholderService();
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request, final Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            Log.info("Lambda received request for API version " + version + ". Request: " + request.toString());

            placeholderService.process(request.toString());
            String body = String.format("{ \"message\": \"hello world\", \"version\": \"%s\" }", version);
            response = generateResponse(200, body);

            Log.info("Completed processing request");
            return response;
        } catch (Exception ex) {
            Log.error("Exception in handler while processing request", ex);
            response = generateResponse(500, "{ \"message\": \"Error\" }");
            return response;
        } finally {
            Log.info("Lambda Handler is returning response: " + response);
        }
    }

    private APIGatewayProxyResponseEvent generateResponse(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
        headers.put("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        headers.put("Access-Control-Allow-Credentials", "true");

        return new APIGatewayProxyResponseEvent()
                        .withStatusCode(statusCode)
                        .withHeaders(headers)
                        .withBody(body);
    }
}
