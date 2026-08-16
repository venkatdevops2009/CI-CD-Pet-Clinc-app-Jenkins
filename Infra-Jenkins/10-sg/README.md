# 10-sg — PetClinic security group foundation

This Terraform layer creates the base security groups for the PetClinic platform. It is intended to be used after the VPC layer so the environment has a network to attach to and SSM values to reference.

## Purpose

The security group layer creates the empty SG resources that represent the application and infrastructure components used by the PetClinic deployment. This separation keeps the network foundation and the firewall rules manageable and allows the environment to grow without forcing rule definition into the same step that creates the SGs.

## What this layer creates

- one security group per target component in the environment
- SSM parameters storing the security group IDs for later use
- a consistent foundation for ingress and egress rules to be added in later infrastructure stages

Typical examples include groups for the application tier, database tier, and admin access paths such as bastion or jump access.

## Depends on

- `00-vpc` — reads `/petclinc/dev/vpc_id` and the subnet values created earlier

## Used by

- later layers that attach security groups to app or database resources
- future ingress/egress rule definitions for the PetClinic environment

## Run

```bash
terraform init
terraform apply
```

## Notes

This repository currently contains the first two core infrastructure layers: VPC creation and SG creation. Additional rule definitions and compute resources can be added as the deployment architecture grows.

