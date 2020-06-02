resource "aws_api_gateway_resource" "contents" {
  path_part   = "contents"
  parent_id   = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ROOT_RESOURCE_ID
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
}

resource "aws_api_gateway_resource" "id_path" {
  path_part   = "{id}"
  parent_id   = aws_api_gateway_resource.contents.id
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
}

resource "aws_api_gateway_method" "get_content" {
  resource_id          = aws_api_gateway_resource.id_path.id
  rest_api_id          = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  http_method          = "GET"
  authorization        = "NONE"
  api_key_required     = true
  request_parameters = {
    "method.request.path.id" = true
  }
}

resource "aws_api_gateway_method_response" "get_content_200" {
  http_method = aws_api_gateway_method.get_content.http_method
  resource_id = aws_api_gateway_resource.id_path.id
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  status_code = "200"
}

resource "aws_api_gateway_integration" "get_content" {
  http_method             = aws_api_gateway_method.get_content.http_method
  resource_id             = aws_api_gateway_resource.id_path.id
  rest_api_id             = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  type                    = "AWS_PROXY"
  integration_http_method = "POST"
  uri                     = aws_lambda_function.content_logic.invoke_arn
  passthrough_behavior    = "WHEN_NO_MATCH"
  request_parameters = {
    "integration.request.path.id" = "method.request.path.id"
  }
}

resource "aws_api_gateway_method" "delete_content" {
  resource_id          = aws_api_gateway_resource.id_path.id
  rest_api_id          = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  http_method          = "DELETE"
  authorization        = "NONE"
  api_key_required     = true
  request_parameters = {
    "method.request.path.id" = true
  }
}

resource "aws_api_gateway_method_response" "delete_content_200" {
  http_method = aws_api_gateway_method.delete_content.http_method
  resource_id = aws_api_gateway_resource.id_path.id
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  status_code = "200"
}

resource "aws_api_gateway_integration" "delete_content" {
  http_method             = aws_api_gateway_method.delete_content.http_method
  resource_id             = aws_api_gateway_resource.id_path.id
  rest_api_id             = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  type                    = "AWS_PROXY"
  integration_http_method = "POST"
  uri                     = aws_lambda_function.content_logic.invoke_arn
  passthrough_behavior    = "WHEN_NO_MATCH"
  request_parameters = {
    "integration.request.path.id" = "method.request.path.id"
  }
}

resource "aws_api_gateway_resource" "search" {
  path_part   = "search"
  parent_id   = aws_api_gateway_resource.contents.id
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
}

resource "aws_api_gateway_method" "search_contents" {
  resource_id          = aws_api_gateway_resource.search.id
  rest_api_id          = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  http_method          = "GET"
  authorization        = "NONE"
  api_key_required     = true
  request_validator_id = aws_api_gateway_request_validator.search_parameters.id
  request_parameters = {
    "method.request.querystring.name" = true
  }
}

resource "aws_api_gateway_request_validator" "search_parameters" {
  rest_api_id                 = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  name                        = "Validate search request parameters"
  validate_request_parameters = true
}

resource "aws_api_gateway_method_response" "search_contents_200" {
  http_method = aws_api_gateway_method.search_contents.http_method
  resource_id = aws_api_gateway_resource.search.id
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  status_code = "200"
}

resource "aws_api_gateway_integration" "search_contents" {
  http_method             = aws_api_gateway_method.search_contents.http_method
  resource_id             = aws_api_gateway_resource.search.id
  rest_api_id             = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  type                    = "AWS_PROXY"
  integration_http_method = "POST"
  uri                     = aws_lambda_function.content_logic.invoke_arn
}

resource "aws_api_gateway_method" "get_all_contents" {
  resource_id          = aws_api_gateway_resource.contents.id
  rest_api_id          = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  http_method          = "GET"
  authorization        = "NONE"
  api_key_required     = true
}

resource "aws_api_gateway_method_response" "get_all_contents_200" {
  http_method = aws_api_gateway_method.get_all_contents.http_method
  resource_id = aws_api_gateway_resource.contents.id
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  status_code = "200"
}

resource "aws_api_gateway_integration" "get_all_contents" {
  http_method             = aws_api_gateway_method.get_all_contents.http_method
  resource_id             = aws_api_gateway_resource.contents.id
  rest_api_id             = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  type                    = "AWS_PROXY"
  integration_http_method = "POST"
  uri                     = aws_lambda_function.content_logic.invoke_arn
}

resource "aws_api_gateway_request_validator" "content_body" {
  rest_api_id           = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  name                  = "content"
  validate_request_body = false
}

resource "aws_api_gateway_method" "create_content" {
  resource_id          = aws_api_gateway_resource.contents.id
  rest_api_id          = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  http_method          = "POST"
  authorization        = "NONE"
  api_key_required     = true
  request_validator_id = aws_api_gateway_request_validator.content_body.id
}

resource "aws_api_gateway_integration" "create_content" {
  http_method             = aws_api_gateway_method.create_content.http_method
  resource_id             = aws_api_gateway_resource.contents.id
  rest_api_id             = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  type                    = "AWS_PROXY"
  integration_http_method = "POST"
  uri                     = aws_lambda_function.content_logic.invoke_arn
}

resource "aws_api_gateway_method" "update_content" {
  resource_id          = aws_api_gateway_resource.contents.id
  rest_api_id          = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  http_method          = "PUT"
  authorization        = "NONE"
  api_key_required     = true
  request_validator_id = aws_api_gateway_request_validator.content_body.id
}

resource "aws_api_gateway_integration" "update_content" {
  http_method             = aws_api_gateway_method.update_content.http_method
  resource_id             = aws_api_gateway_resource.contents.id
  rest_api_id             = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  type                    = "AWS_PROXY"
  integration_http_method = "POST"
  uri                     = aws_lambda_function.content_logic.invoke_arn
}

resource "aws_api_gateway_method_settings" "contents" {
  method_path = "*/*"
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
  stage_name  = "api${local.environment_suffix}" // must be addded to platform outputs

  settings {
    metrics_enabled = true
    logging_level = "ERROR"
    data_trace_enabled = true
  }
}

resource "aws_api_gateway_deployment" "contents" {
  rest_api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID

  variables = {
    deployed_at = timestamp()
  }

  depends_on = [
    aws_api_gateway_integration.get_all_contents,
    aws_api_gateway_integration.get_content,
    aws_api_gateway_integration.search_contents,
    aws_api_gateway_integration.create_content,
    aws_api_gateway_integration.update_content,
    aws_api_gateway_integration.delete_content
  ]
}

resource "aws_api_gateway_usage_plan" "contents" {
  name = "aws_api_usage_plan_contents${local.environment_suffix}"

  api_stages {
    api_id = data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_ID
    stage  = "api${local.environment_suffix}" // must be addded to platform outputs
  }
}

resource "aws_api_gateway_usage_plan_key" "contents" {
  key_id        = aws_api_gateway_api_key.contents.id
  usage_plan_id = aws_api_gateway_usage_plan.contents.id
  key_type      = "API_KEY"
}

resource "aws_api_gateway_api_key" "contents" {
  name = "aws_api_gateway_api_key_contents${local.environment_suffix}"
}

resource "aws_lambda_permission" "contents" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.content_logic.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${data.terraform_remote_state.platform_infrastructure.outputs.AWS_API_GATEWAY_EXECUTION_ARN}/*/*"
}
