# Check naming best practice here (https://www.terraform-best-practices.com/naming#variables)
variable "environment" {
  description = "Environment to deploy to"
}

variable "project" {
  description = "Project name to use"
  default = "content-service"
}

variable "domain" {
  description = "Domain to use"
  default = "content-es"
}

variable "contents_dynamo_table_name" {
  description = "DynamoDB contents table name"
  default     = "contents-table"
}

variable "kinesis_shard_count" {
  description = "Number of shards to deploy in the stream"
  default     = 1
}

variable "kinesis_retention_period" {
  description = "Retention period for the data in the kinesis stream"
  default     = 24
}

variable "kinesis_contents_stream" {
  description = "Contents Kinesis stream name"
  default     = "contents-stream"
}

variable "contents_logic_lambda_handler" {
  description = "Contents Logic Lambda handler class"
  default     = "com.service.content.platform.StreamLambdaHandler"
}

variable "contents_trigger_lambda_handler" {
  description = "Contents Trigger Lambda handler class"
  default     = "com.service.content.trigger.lambda.ContentTriggerHandler"
}

variable "role_arn" {
  description = "AWS IAM Role to assume in the Platform AWS account"
}
