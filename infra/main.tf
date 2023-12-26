provider "aws" {
  region = var.region
}

// Terraform Core: Reads all .tf files / configurations and builds the resource dependency graph
// Terraform Plugins (required_providers): Provide APIs to modify AWS, Azure, or for simple modules such as a random string generator
terraform {
  cloud {
    organization = "mcmasters"

    # Switching between workspaces is how you deploy to dev, test, etc
    workspaces {
      
      # Allows you to use all workspaces that have this tag in Terraform Cloud
      # Run `terraform workspace list` to view these workspaces, or `terraform workspace select <WORKSPACE>` to switch to one
      # There should be a workspace for dev and test
      tags = ["website-and-infra-2"]
      
    }
  }
  
  required_providers {
    // You can find this information on this page (on the right, click Providers to see a code example: https://registry.terraform.io/providers/hashicorp/aws/latest)
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.17.0"
    }
  }

  required_version = ">= 1.4.6"
}