# This page declares variables and gives default values
# Vars in ./infra/environments/dev.tfvars and ./infra/environments/test.tfvars will override them

variable "env" {
  default = "dev"
}

variable "region" {
  default = "us-east-1"
  description = "Optionally say something about this variable"
}

variable "project_tag" {
  default = "website-and-infra-2"
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

# If you change this value, make sure to change it in ./backend/src/main/resources/local.yml, dev.yml, and test.yml also
variable "dynamodb_key" {
  type = string
  description = "The name of the key for each row of data in DynamoDb. This key value must be supplied when adding or retreiving items from the database"
  default = "TestTableHashKey"
}

variable "s3_bucket_name" {
  default = "website-and-infra-2"
}

variable "oac_name" {
  type = string
  description = "Origin Access Control (OAC): A permission (similar to IAM) that allows CloudFront to access your S3 bucket, even when it is a private bucket"
  default = "website-and-infra-2-oac"
}