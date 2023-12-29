# Logs API Gateway's endpoint
output "api_gateway_base_url" {
  value = "${aws_api_gateway_deployment.default.invoke_url}"
}

# Logs the website's domain
output "cloudfront_url" {
  value = aws_cloudfront_distribution.cdn_static_site.domain_name
}

output "lambda_name" {
  value = aws_lambda_function.default.function_name
}

output "s3_bucket_name" {
  value = aws_s3_bucket.website_bucket.bucket
}