# Logs API Gateway's endpoint
output "api_gateway_base_url" {
  value = "${aws_api_gateway_deployment.default.invoke_url}"
}

# Logs the website's domain
output "cloudfront_url" {
  value = aws_cloudfront_distribution.cdn_static_site.domain_name
}