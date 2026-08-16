locals {
    petclinc_sg_id = data.aws_ssm_parameter.petclinc_sg_id.value    
    jenkins_sg_id = data.aws_ssm_parameter.jenkins_sg_id.value
    jenkins_agent_sg_id = data.aws_ssm_parameter.jenkins_agent_sg_id.value
    sonar_sg_id = data.aws_ssm_parameter.sonar_sg_id.value
}