data "aws_ssm_parameter" "petclinc_sg_id" {
  name = "/${var.project}/${var.environment}/petclinc_sg_id"
}

data "aws_ssm_parameter" "jenkins_sg_id" {
  name = "/${var.project}/${var.environment}/jenkins_sg_id"
}

data "aws_ssm_parameter" "jenkins_agent_sg_id" {
  name = "/${var.project}/${var.environment}/jenkins_agent_sg_id"
}

data "aws_ssm_parameter" "sonar_sg_id" {
  name = "/${var.project}/${var.environment}/sonar_sg_id"
}

data "http" "my_public_ip" {
  url = "https://ipv4.icanhazip.com"
}