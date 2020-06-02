terraform {
  backend "s3" {
    region = "eu-west-1"
    bucket = "terraform-state-content-service-3b84f6c6"
    key    = "state"
  }
}
