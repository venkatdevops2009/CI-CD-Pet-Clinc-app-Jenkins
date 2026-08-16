module "vpc" {
  source              = "git::https://github.com/venkatdevops2009/terraform-aws-vpc-module.git?ref=main"
  project             = var.project
  environment         = var.environment
  is_peering_required = false
  is_nat_required     = false
}