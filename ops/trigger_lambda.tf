resource "aws_iam_role" "content_trigger" {
  name = "content_trigger_role${local.environment_suffix}"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow"
    },
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "dynamodb.amazonaws.com"
      },
      "Effect": "Allow"
    },
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "kinesisanalytics.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
  EOF
}

resource "aws_iam_policy" "content_trigger" {
  name = "content_trigger_service_policy${local.environment_suffix}"
  path = "/"
  description = "IAM policy for ContentService Trigger Lambda"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:*:*",
      "Effect": "Allow"
    },
    {
      "Action": [
        "kinesis:GetShardIterator",
        "kinesis:DescribeStream",
        "kinesis:GetRecords",
        "kinesis:PutRecord",
        "kinesis:PutRecords"
      ],
      "Resource": "arn:aws:kinesis:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:stream/${var.kinesis_contents_stream}${local.environment_suffix}",
      "Effect": "Allow"
    },
    {
      "Action": [
        "dynamodb:GetRecords",
        "dynamodb:GetShardIterator",
        "dynamodb:DescribeStream",
        "dynamodb:ListStreams"
      ],
      "Resource": "arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/${var.contents_dynamo_table_name}${local.environment_suffix}/stream/*",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "content_trigger" {
  role       = aws_iam_role.content_trigger.name
  policy_arn = aws_iam_policy.content_trigger.arn
}


resource "aws_s3_bucket_object" "trigger_lambda" {
  key                    = "content_trigger.zip"
  bucket                 = aws_s3_bucket.lambda.id
  source                 = "../content-triggers/target/content_trigger.zip"
  etag                   = filemd5("../content-triggers/target/content_trigger.zip")
  acl                    = "public-read"
}

resource "aws_lambda_layer_version" "trigger_lambda" {
  s3_bucket   = aws_s3_bucket.lambda.id
  layer_name = "trigger_lambda_layer${local.environment_suffix}"
  s3_key = aws_s3_bucket_object.trigger_lambda.key
  s3_object_version = aws_s3_bucket_object.trigger_lambda.version_id

  compatible_runtimes = [
    "java8"
  ]
}

resource "aws_lambda_function" "content_trigger" {
  s3_bucket        = aws_s3_bucket.lambda.id
  s3_key           = "content_trigger.zip"
  function_name    = "content_trigger${local.environment_suffix}"
  source_code_hash = filebase64sha256("../content-triggers/target/content_trigger.zip")
  role             = aws_iam_role.content_trigger.arn
  handler          = var.contents_trigger_lambda_handler
  timeout          = 50
  runtime          = "java8"
  layers           = [
    aws_lambda_layer_version.trigger_lambda.arn
  ]
  memory_size = 1024
  tags = local.common_tags

  depends_on = [
    aws_dynamodb_table.contents
  ]

  environment {
    variables = {
      MAIN_CLASS = "com.service.content.trigger.ContentTriggerApplication"
    }
  }

}

resource "aws_kinesis_stream" "contents" {
  name             = "${var.kinesis_contents_stream}${local.environment_suffix}"
  retention_period = var.kinesis_retention_period

  shard_count      = var.kinesis_shard_count
  shard_level_metrics = [
    "IncomingBytes",
    "OutgoingBytes",
  ]

  tags = local.common_tags
}

resource "aws_lambda_event_source_mapping" "trigger_lambda_event" {
  event_source_arn  = aws_dynamodb_table.contents.stream_arn
  function_name     = aws_lambda_function.content_trigger.arn
  starting_position = "LATEST"
}
