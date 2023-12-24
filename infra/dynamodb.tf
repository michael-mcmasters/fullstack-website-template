resource "aws_dynamodb_table" "default" {
  name        = var.dynamodb_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key        = "TestTableHashKey"
  
  attribute {
    name = "TestTableHashKey"
    type = "S"
  }
  
  tags = {
    project       = var.project_tag
  }
}