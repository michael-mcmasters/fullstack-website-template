locals {
  # Unfortunately, you can't deploy a Lambda without a zip file. So we deploy a dummy jar and then upload the real codebase through the CLI (not in this repo)
  # Source: https://www.reddit.com/r/Terraform/comments/j7dpqq/store_lambda_function_code_separate_from_tf_code/
  jar_file = "./lambda-placeholder-code.jar"
}

# The actual Lambda
resource "aws_lambda_function" "default" {
  filename = local.jar_file
  function_name = var.lambda_name
  role          = aws_iam_role.lambda_role.arn
  handler       = "org.mcmasters.Handler::handleRequest"

  source_code_hash = "${base64sha256(filebase64(local.jar_file))}"

  runtime = "java17"

  environment {
    variables = {
      foo = "bar"
    }
  }
}

# The IAM role the lambda assumes
resource "aws_iam_role" "lambda_role" {
  name               = var.lambda_role_name
  
  # Adds a basic policy to the role
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Sid    = ""
      Principal = {
        Service = "lambda.amazonaws.com"
      }
      }
    ]
  })
}

# Adds another policy to the role - AWSLambdaBasicExcecutionRole, which allows the Lambda to create Cloudwtach logs among other things
resource "aws_iam_role_policy_attachment" "basic_execution_role_policy" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# The Cloudwatch Log Group
resource "aws_cloudwatch_log_group" "lambda_log_group" {
  name = "/aws/lambda/${aws_lambda_function.default.function_name}"

  retention_in_days = 30
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