# This file is based off of this tutorial: https://registry.terraform.io/providers/hashicorp/aws/2.34.0/docs/guides/serverless-with-aws-lambda-and-api-gateway

# Acts as a container for all API Gateway resources being created
resource "aws_api_gateway_rest_api" "default" {
  name        = var.api_gateway_name
  description = "Terraform Serverless Application Example"
}

# All requests must match this resource
resource "aws_api_gateway_resource" "proxy" {
  rest_api_id = "${aws_api_gateway_rest_api.default.id}"
  parent_id   = "${aws_api_gateway_rest_api.default.root_resource_id}"
  path_part   = "{proxy+}"  # proxy+ means any path will match this resource
}

# And this method
resource "aws_api_gateway_method" "proxy" {
  rest_api_id   = "${aws_api_gateway_rest_api.default.id}"
  resource_id   = "${aws_api_gateway_resource.proxy.id}"
  http_method   = "ANY"
  authorization = "NONE"
}

# Tells the request which lambda to trigger
# Each HTTP method will have its own integration
resource "aws_api_gateway_integration" "lambda" {
  rest_api_id = "${aws_api_gateway_rest_api.default.id}"
  resource_id = "${aws_api_gateway_method.proxy.resource_id}"
  http_method = "${aws_api_gateway_method.proxy.http_method}"

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "${aws_lambda_function.default.invoke_arn}"
}

# I barely understand the next two portions, but I'll try to explain
# API Gateway service calls the Lambda service to invoke it. To allow this, we need to create another method and integration.
# (See this page for details: https://registry.terraform.io/providers/hashicorp/aws/2.34.0/docs/guides/serverless-with-aws-lambda-and-api-gateway)
# TODO: Reading that page, I'm wondering if this is needed if I'll have endpoints. This seems to only be a workaround for the lambda to be called if I call the root endpoint with no additional path
resource "aws_api_gateway_method" "proxy_root" {
  rest_api_id   = "${aws_api_gateway_rest_api.default.id}"
  resource_id   = "${aws_api_gateway_rest_api.default.root_resource_id}"    # Notice this is the root resource, which sits at the top of the REST API object
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda_root" {
  rest_api_id = "${aws_api_gateway_rest_api.default.id}"
  resource_id = "${aws_api_gateway_method.proxy_root.resource_id}"
  http_method = "${aws_api_gateway_method.proxy_root.http_method}"

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "${aws_lambda_function.default.invoke_arn}"
}

# Deploys these endpoints. Here we are deploying a "test" stage. But you can deploy multiple stages such as dev, perf, prod, etc.
resource "aws_api_gateway_deployment" "default" {
  depends_on = [
    "aws_api_gateway_integration.lambda",
    "aws_api_gateway_integration.lambda_root"
    # "aws_cloudwatch_log_group.apigw_log_group"
  ]

  rest_api_id = "${aws_api_gateway_rest_api.default.id}"
  stage_name  = "test"
}

resource "aws_api_gateway_method_settings" "example" {
  rest_api_id = aws_api_gateway_rest_api.default.id
  stage_name  = aws_api_gateway_deployment.default.stage_name
  method_path = "*/*"

  settings {
    metrics_enabled = true
    logging_level   = "INFO"
  }
}

# Allows API Gateway to invoke Lambda
resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.default.function_name}"
  principal     = "apigateway.amazonaws.com"

  # The /*/* portion grants access from any method on any resource
  # within the API Gateway "REST API".
  source_arn = "${aws_api_gateway_rest_api.default.execution_arn}/*/*"
}