# 00-vpc — PetClinic AWS network foundation

This is the first Terraform layer in the repository. It creates the base network for the PetClinic environment and stores the key values in AWS Systems Manager Parameter Store so later layers can read them without hard-coding IDs.

## Purpose

The VPC layer creates the networking foundation used by the application and related services in the PetClinic AWS deployment. In the current repository, this is the starting point for the infrastructure stack and provides:

- a VPC for the environment
- public, private, and database subnet groups
- internet and routing components needed for access and isolation
- SSM parameters for later infrastructure layers to consume

## What this layer creates

- VPC and subnet layout for the PetClinic environment
- route tables and gateways required for connectivity
- parameter values such as:
  - `/petclinc/dev/vpc_id`
  - `/petclinc/dev/public_subnet_ids`
  - `/petclinc/dev/private_subnet_ids`
  - `/petclinc/dev/database_subnet_ids`

These values are used by the following Terraform layers and deployment resources.

## Depends on

- Nothing. This is the first layer.

## Used by

- `10-sg` for security group placement inside the VPC
- any future application, database, or load balancer layers that need the VPC ID and subnet IDs

## Run

```bash
terraform init
terraform apply
```

## Notes

This repository currently contains the network and security-group starting blocks for the PetClinic infrastructure demo. Additional Terraform layers can be added later as the AWS deployment grows.

