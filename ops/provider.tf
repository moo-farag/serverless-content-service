provider "archive" {
  version = "~> 1.2"
}

provider "aws" {
  version = "~> 2.2"
  region  = "eu-west-1"
  assume_role {
    role_arn = var.role_arn
  }
}
