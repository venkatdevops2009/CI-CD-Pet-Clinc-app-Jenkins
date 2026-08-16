module "sg" {
  count  = length(var.sg_names)
  source = "../modules/sg"

  project     = var.project
  environment = var.environment
  vpc_id      = local.vpc_id
  sg_name     = replace(var.sg_names[count.index], "_", "-")
}