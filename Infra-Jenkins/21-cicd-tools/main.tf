resource "aws_instance" "jenkins" {
  count                  = var.jenkins ? 1 : 0
  ami                    = local.ami_id
  instance_type          = "t3.small"
  subnet_id              = local.public_subnet_id
  vpc_security_group_ids = [local.jenkins_sg_id]
  user_data              = file("jenkins.sh")

  metadata_options {
    http_tokens                 = "required"
    http_endpoint               = "enabled"
    http_put_response_hop_limit = 2
  }

  root_block_device {
    volume_size = 50
    volume_type = "gp3"
    encrypted   = true
    tags = merge(
      {
        Name = "${var.project}-${var.environment}-jenkins"
      },
      local.common_tags
    )
  }
  instance_market_options {
    market_type = "spot"
    spot_options {
      max_price                      = "0.040"
      spot_instance_type             = "one-time"
      instance_interruption_behavior = "terminate"
    }
  }

  tags = merge(
    {
      Name = "${var.project}-${var.environment}-jenkins"
    },
    local.common_tags
  )
}


resource "aws_instance" "jenkins_agent" {
  count                  = var.jenkins ? 1 : 0
  ami                    = local.ami_id
  instance_type          = "t3.micro"
  subnet_id              = local.public_subnet_id
  vpc_security_group_ids = [local.jenkins_agent_sg_id]
  user_data              = file("jenkins-agent.sh")

  metadata_options {
    http_tokens                 = "required"
    http_endpoint               = "enabled"
    http_put_response_hop_limit = 2
  }

  root_block_device {
    volume_size = 50
    volume_type = "gp3"
    encrypted   = true
    tags = merge(
      {
        Name = "${var.project}-${var.environment}-jenkins-agent"
      },
      local.common_tags
    )
  }

  tags = merge(
    {
      Name = "${var.project}-${var.environment}-jenkins-agent"
    },
    local.common_tags
  )
}

resource "aws_instance" "sonarqube" {
  count                  = var.sonar ? 1 : 0
  ami                    = local.sonar_ami_id
  instance_type          = "t3.large"
  vpc_security_group_ids = [local.sonar_sg_id]
  subnet_id              = local.public_subnet_id #replace your Subnet in default VPC
  key_name               = "my-key"

  metadata_options {
    http_tokens                 = "required"
    http_endpoint               = "enabled"
    http_put_response_hop_limit = 2
  }

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
    encrypted   = true
    tags = merge(
      local.common_tags,
      {
        Name = "${var.project}-${var.environment}-sonar"
      }
    )
  }

  instance_market_options {
    market_type = "spot"
    spot_options {
      max_price                      = "0.040"
      spot_instance_type             = "one-time"
      instance_interruption_behavior = "terminate"
    }
  }
  tags = merge(
    local.common_tags,
    {
      Name = "${var.project}-${var.environment}-sonar"
    }
  )
}