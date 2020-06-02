locals {
  environment_name   = var.environment == "production" ? "" : var.environment
  environment_suffix = var.environment == "production" ? "" : format("-%s", local.environment_name)
  short_name         = "content-service"
  name               = "${var.project}${local.environment_suffix}"

  common_tags = {
    Project     = var.project
    Environment = var.environment
  }
}

data "aws_region" "current" {
}

data "aws_caller_identity" "current" {
}

data "terraform_remote_state" "platform_infrastructure" {
  backend                       = "s3"
  config = {
    bucket                      = "terraform-state-platform-9226ba2e" //platform backend bucket
    key                         = "state"
  }
  workspace = var.environment
}

resource "aws_dynamodb_table" "contents" {
  name           = "${var.contents_dynamo_table_name}${local.environment_suffix}"
  hash_key       = "id"
  billing_mode   = local.environment_suffix == "production" ? "PROVISIONED" : "PAY_PER_REQUEST"
  read_capacity  = local.environment_suffix == "production" ? 12 : null
  write_capacity = local.environment_suffix == "production" ? 48 : null
  stream_enabled = true
  stream_view_type = "NEW_IMAGE"
  attribute {
    name = "id"
    type = "S"
  }
}

resource "aws_elasticsearch_domain" "es" {
  domain_name           = var.domain
  elasticsearch_version = "7.1"

  cluster_config {
    instance_type = "t2.small.elasticsearch"
    zone_awareness_enabled = false
    instance_count = 1

  }

  ebs_options {
    ebs_enabled = true
    volume_type = "gp2"
    volume_size = 10
  }

  advanced_options = {
    "rest.action.multi.allow_explicit_index" = "true"
  }

  snapshot_options {
    automated_snapshot_start_hour = 23
  }

}

resource "aws_iam_role" "content_logic" {
  name = "content_logic_role${local.environment_suffix}"
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
        "Service": "es.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
  EOF
}

resource "aws_iam_policy" "content_logic" {
  name = "content_logic_service_policy${local.environment_suffix}"
  path = "/"
  description = "IAM policy for ContentService Logic Lambda"
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
        "dynamodb:BatchGet*",
        "dynamodb:DescribeStream",
        "dynamodb:DescribeTable",
        "dynamodb:Get*",
        "dynamodb:Query",
        "dynamodb:Scan",
        "dynamodb:BatchWrite*",
        "dynamodb:Delete*",
        "dynamodb:Update*",
        "dynamodb:PutItem"
      ],
      "Resource": "arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/${var.contents_dynamo_table_name}${local.environment_suffix}",
      "Effect": "Allow"
    },
    {
      "Action": "es:*",
      "Resource": "arn:aws:es:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:domain/${var.domain}/*",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "content_logic" {
  role       = aws_iam_role.content_logic.name
  policy_arn = aws_iam_policy.content_logic.arn
}
