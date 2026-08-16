variable "project" {
  default = "petclinc"
}

variable "environment" {
  default = "dev"
}

variable "sg_names" {
  type = list(any)
  default = [
    "petclinc",
    "jenkins",
    "jenkins_agent",
    "sonar"
  ]
}