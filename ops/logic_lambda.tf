resource "aws_s3_bucket" "lambda" {
  bucket = "content-service-lambda"
  acl    = "private"
}

resource "aws_s3_bucket_object" "logic_lambda" {
  key                    = "content_logic.zip"
  bucket                 = aws_s3_bucket.lambda.id
  source                 = "../content-logic/target/content_logic.zip"
  etag                   = filemd5("../content-logic/target/content_logic.zip")
  acl                    = "public-read"
}

resource "aws_lambda_layer_version" "logic_lambda" {
  s3_bucket   = aws_s3_bucket.lambda.id
  layer_name = "logic_lambda_layer${local.environment_suffix}"
  s3_key = aws_s3_bucket_object.logic_lambda.key
  s3_object_version = aws_s3_bucket_object.logic_lambda.version_id

  compatible_runtimes = [
    "java8"
  ]
}

resource "aws_lambda_function" "content_logic" {
  s3_bucket        = aws_s3_bucket.lambda.id
  s3_key           = "content_logic.zip"
  function_name    = "content_logic${local.environment_suffix}"
  source_code_hash = filebase64sha256("../content-logic/target/content_logic.zip")
  role             = aws_iam_role.content_logic.arn
  handler          = var.contents_logic_lambda_handler
  timeout          = 50
  runtime          = "java8"
  layers           = [
    aws_lambda_layer_version.logic_lambda.arn
  ]
  memory_size = 1024
  tags = local.common_tags

  depends_on = [
    aws_elasticsearch_domain.es
  ]

  environment {
    variables = {
      AWS_ELASTICSEARCH_ENDPOINT = aws_elasticsearch_domain.es.endpoint,
      AWS_ELASTICSEARCH_NAME = "es"
    }
  }

}

