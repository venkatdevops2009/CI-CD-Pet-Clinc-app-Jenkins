
# Jenkins
resource "aws_security_group_rule" "jenkins_public" {
  type              = "ingress"
  from_port         = 8080
  to_port           = 8080
  protocol          = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  #["${chomp(data.http.my_public_ip.response_body)}/32"]
  security_group_id = local.jenkins_sg_id
}

resource "aws_security_group_rule" "jenkins_ssh" {
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  cidr_blocks = ["${chomp(data.http.my_public_ip.response_body)}/32"]
  security_group_id = local.jenkins_sg_id
}

resource "aws_security_group_rule" "jenkins_agent_ssh" {
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  cidr_blocks = ["${chomp(data.http.my_public_ip.response_body)}/32"]
  security_group_id = local.jenkins_agent_sg_id
}

resource "aws_security_group_rule" "jenkins_agent_jenkins" {
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  source_security_group_id = local.jenkins_sg_id
  security_group_id = local.jenkins_agent_sg_id
}

resource "aws_security_group_rule" "jenkins_agent_web" {
  type              = "ingress"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = local.jenkins_agent_sg_id
}

resource "aws_security_group_rule" "sonar_web" {
  type              = "ingress"
  from_port         = 9000
  to_port           = 9000
  protocol          = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = local.sonar_sg_id
}

resource "aws_security_group_rule" "sonar_ssh" {
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  cidr_blocks = ["${chomp(data.http.my_public_ip.response_body)}/32"]
  security_group_id = local.sonar_sg_id
}
