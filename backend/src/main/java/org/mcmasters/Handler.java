package org.mcmasters;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.mcmasters.service.EnvironmentConfigService;
import org.mcmasters.service.RequestService;
import org.mcmasters.util.Log;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String apiVersion = "11";

    private RequestService requestService;

    private EnvironmentConfigService environmentConfigService;


    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request, final Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            Log.info("Lambda received request for API version " + apiVersion + ". Request: " + request.toString());
            handleDependencyInjection();
            environmentConfigService.setupConfig();

            String message = requestService.process(request.toString());
            String body = String.format("{ \"message\": \"%s\", \"version\": \"%s\" }", message, apiVersion);
            response = generateResponse(200, body);

            Log.info("Completed processing request");
            return response;
        } catch (Exception ex) {
            Log.error("Exception in handler while processing request", ex);
            String body = String.format("{ \"message\": \"Error: %s\", \"version\": \"%s\" }", ex, apiVersion);
            response = generateResponse(500, body);
            return response;
        } finally {
            Log.info("Lambda Handler is returning response: " + response);
        }
    }

    /**
     * Running Dependency injection here instead of in the Handler constructor to make sure handleRequest() catches any exceptions a dependency's constructor may throw
     * Otherwise Lambda may return an invalid response
     */
    private void handleDependencyInjection() {
        if (this.requestService == null) {
            this.requestService = new RequestService();
        }
        if (this.environmentConfigService == null) {
            this.environmentConfigService = new EnvironmentConfigService();
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
