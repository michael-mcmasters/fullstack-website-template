resource "aws_dynamodb_table" "default" {
  name        = "${var.dynamodb_name}-${var.env}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key        = var.dynamodb_key
  
  attribute {
    name = var.dynamodb_key
    type = "S"
  }
  
  tags = {
    project       = var.project_tag
  }
}