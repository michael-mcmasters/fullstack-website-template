variable "project_tag" {
  default = "website-and-infra-2"
}

variable "env" {
  default = "dev"
}

variable "api_gateway_name" {
  default = "website-and-infra-2-apigw"
}

variable "api_gateway_role_name" {
  default = "website-and-infra-2-apigw-role"
}

variable "lambda_name" {
  default = "website-and-infra-2-lambda"
}

variable "lambda_runtime" {
  default = "java17"
}

variable "lambda_role_name" {
  default = "website-and-infra-2-lambda-role"
}

variable "lambda_role_policy_to_dynamodb_name" {
  default = "website-and-infra-2-lambda-to-dynamodb-policy"
}

variable "dynamodb_name" {
  default = "website-and-infra-2-dynamodb"
}

variable "s3_bucket_name" {
  default = "website-and-infra-2"
}

variable "oac_name" {
  default = "website-and-infra-2-oac"
}

variable "region" {
  default = "us-east-1"
  description = "Optionally say something about this variable"
}