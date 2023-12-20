provider "aws" {
  region = "us-east-1"
}

// Terraform Core: Reads all .tf files / configurations and builds the resource dependency graph
// Terraform Plugins (required_providers): Provide APIs to modify AWS, Azure, or for simple modules such as a random string generator
terraform {
  cloud {
    organization = "mcmasters"

    workspaces {
      name = "website-and-infra-2-ui"
    }
  }
  
  required_providers {
    // You can find this information on this page (on the right, click Providers to see a code example: https://registry.terraform.io/providers/hashicorp/aws/latest)
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.17.0"
    }
  }

  required_version = "~> 1.4"
}