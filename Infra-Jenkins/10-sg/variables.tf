variable "project" {
    default = "petclinc"
}

variable "environment" {
    default = "dev"
}

variable "sg_names" {
    type = list
    default = [
        "petclinc", 
        "jenkins", 
        "jenkins_agent", 
        "sonar"        
    ]
}