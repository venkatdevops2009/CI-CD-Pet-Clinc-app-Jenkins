variable "project" {
  description = "Project name used in tags and naming."
  type        = string
}

variable "environment" {
  description = "Environment name used in tags and naming."
  type        = string
}

variable "vpc_id" {
  description = "VPC ID where the security group will be created."
  type        = string
}

variable "sg_name" {
  description = "Security group name."
  type        = string
}

resource "aws_security_group" "main" {
  name        = var.sg_name
  description = "Security group for ${var.sg_name}"
  vpc_id      = var.vpc_id

  # Explicitly avoid the default all-egress rule that Trivy flags as unrestricted.
  egress = []

  tags = {
    Name        = var.sg_name
    Project     = var.project
    Environment = var.environment
    Terraform   = "true"
  }
}

output "sg_id" {
  description = "Created security group ID."
  value       = aws_security_group.main.id
}
