## About

This repo serves as a template for quickly deploying serverless fullstack websites to AWS within 5 minutes.

It encompasses Terraform, AWS CloudFront, API Gateway, Lambda, S3, React and Core Java all in one.

These README instructions are specific for my usecase but should work for anyone with minor adjustments. Simply create a Terraform Cloud username and an organization (it's free!). Then replace my name from the CLI commands and Terraform link below.

## Setup Terraform

Run this command to get your AWS Access Key and Secret Key
```
cat /Users/michaelmcmasters/.aws/credentials
```

Go to Terraform Cloud: https://app.terraform.io/app/mcmasters/workspaces
<br />
Create two new workspaces, one for the Backend and one for the UI
<br />
In **each** workspace, on the left sidebar, click "Variables":

Add AWS_ACCESS_KEY_ID,
<br />
Set it to aws_access_key_id's value (from the cat command above),
<br />
Click Environment Variable,
<br />
Check "sensitive"

Add AWS_SECRET_ACCESS_KEY,
<br />
Set it to aws_secret_access_key's value (from the cat command above),
<br />
Click Environment Variable,
<br />
Check "sensitive"

Login to Terraform Cloud from your CLI
```
terraform login
```
It will open the browser prompting you to login, then will give you a token that you paste back into your CLI


## Deploy the Backend

In ./infra-backend/main.tf, set workspaces.name to the backend workspace you created
<br />
In ./infra-backend/variables.tf, set the values so they are unique in your Cloud environement
<br />
cd into ./infra-ui, initialize Terraform, and apply
```
cd infra-backend
terraform init
terraform apply
```


## Deploy the UI

In ./infra-ui/main.tf, set workspaces.name to the UI workspace you created
<br />
In ./infra-ui/variables.tf, set the values so they are unique in your Cloud environement
<br />
cd into ./infra-ui, initialize Terraform, and apply
```
cd infra-ui
terraform init
terraform apply
```
The CLI will log your website URL

Now deploy the UI code to the new bucket
```
cd ../ui
npm install
npm run build
aws s3 cp ./build s3://<BUCKET_NAME>/ --recursive
```

Go to your website URL and you will see your code deployed, and "hello world" being fetched from the backend lambda.
<br />
`{"message":"hello world","version":"1"}`
