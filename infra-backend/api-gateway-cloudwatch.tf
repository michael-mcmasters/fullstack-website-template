# This resource applies settings to all API Gateways in your cloud environment
# It allows API Gateway to push logs to CloudWatch.
# The generated Log Group will be "API-Gateway-Execution-Logs_<APIGATEWAY_ID>/<STAGE>"" ... (Example: API-Gateway-Execution-Logs_ggi4glh0a0/test)
  # APIGATEWAY_ID can be found in the AWS Console -> API Gateway -> APIs (left sidedrawer) Under the ID column
  # STAGE can be found in the "aws_api_gateway_deployment" resource
resource "aws_api_gateway_account" "main" {
  cloudwatch_role_arn = aws_iam_role.apigw_role.arn
}

# The IAM role assumed by API Gateway
resource "aws_iam_role" "apigw_role" {
  name               = var.api_gateway_role_name
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Sid    = ""
      Principal = {
        Service = "apigateway.amazonaws.com"
      }
      }
    ]
  })
}

# Adds another policy to the role - AmazonAPIGatewayPushToCloudWatchLogs
# This is a policy that comes with AWS and exists here: https://us-east-1.console.aws.amazon.com/iam/home?region=us-east-1#/policies/details/arn%3Aaws%3Aiam%3A%3Aaws%3Apolicy%2Fservice-role%2FAmazonAPIGatewayPushToCloudWatchLogs?section=permissions
resource "aws_iam_role_policy_attachment" "apigw_cloudwatch_policy" {
  role       = aws_iam_role.apigw_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
}