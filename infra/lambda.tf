locals {
  # Unfortunately, you can't deploy a Lambda without a zip/jar file. So we deploy a dummy jar and then upload the real codebase through the CLI (not in this repo)
  # Source: https://www.reddit.com/r/Terraform/comments/j7dpqq/store_lambda_function_code_separate_from_tf_code/
  jar_file = "./lambda-placeholder-code.jar"
}

# The actual Lambda
resource "aws_lambda_function" "default" {
  filename = local.jar_file
  function_name = var.lambda_name
  role          = aws_iam_role.lambda_role.arn
  handler       = "org.mcmasters.Handler::handleRequest"
  timeout       = 121

  runtime = var.lambda_runtime

  environment {
    variables = {
      foo = "bar"
    }
  }
}

# The Lambda Cloudwatch Log Group
resource "aws_cloudwatch_log_group" "lambda_log_group" {
  name = "/aws/lambda/${aws_lambda_function.default.function_name}"
  retention_in_days = 30
}

# The IAM role the lambda assumes
resource "aws_iam_role" "lambda_role" {
  name               = var.lambda_role_name
  
  # Specifies what service is allowed to assume this role
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = "sts:AssumeRole"
        Sid    = ""
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

# A policy that will attach to the role - Allows Lambda to access tbe DynamoDB table
resource "aws_iam_policy" "lambda_dynamodb_policy" {
  name        = var.lambda_role_policy_to_dynamodb_name
  description = "IAM policy for Lambda to access DynamoDb"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "dynamodb:BatchGetItem",
        "dynamodb:GetItem",
        "dynamodb:Query",
        "dynamodb:Scan",
        "dynamodb:BatchWriteItem",
        "dynamodb:PutItem",
        "dynamodb:UpdateItem"
      ],
      "Resource": "arn:aws:dynamodb:us-east-1:959175409202:table/${aws_dynamodb_table.default.name}"
    }
  ]
}
EOF
}

# Attaches the above policy to the role
resource "aws_iam_role_policy_attachment" "lambda_dynamodb_policy" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.lambda_dynamodb_policy.arn
}

# Attaches another policy to the role.
# Since this policy already exists in AWS (called AWSLambdaBasicExcecutionRole), we don't need to define it with an aws_iam_policy like we did for the lambda_dynamodb_policy above.
# This policy allows Lambda to create Cloudwtach logs and do a few other basic functions every Lambda needs
resource "aws_iam_role_policy_attachment" "basic_execution_role_policy" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}