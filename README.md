# About

This repo serves as a template for quickly deploying fullstack websites to AWS within 5 minutes.

These README instructions are specific for my usecase but should work for anyone with minor adjustments. Simply create a Terraform Cloud username and an organization (it's free!). Then replace my name from the CLI commands and Terraform link below.

Technologies:
- Terraform
- React
- Core Java
- API Gateway
- Lambda
- DynamoDB
- CloudWatch
- CloudFront
- Route53
- S3

### 1) Prepare Terraform
We start with preparing Terraform because we can't run the app locally until AWS resources are provisioned (such as DynamoDb).

Run this command to get your AWS Access Key and Secret Key
<br />
```
cat /Users/<YOUR_NAME>/.aws/credentials
```

This project uses Terraform Cloud.

Terraform Cloud remotely deploys our infra and keeps track of all resources in AWS. It consists of 3 items we'll need to create:
- `Organization`: Think of this as your company (even if you don't have one). It will contain all of your company's projects.
- `Project`: A name you want to use for what you're working on. Mostly to help you stay organized. The UI, Backend, infra, all of it. Projects contain workspaces.
- `Workspace`: Manages and deploys infra for an environment. You'll have a workspace for dev, test, prod, etc.

First, go to Terraform Cloud (https://app.terraform.io) and create an account if you don't have one.

To create an organization, go to https://app.terraform.io/app/organizations/new -> Give it any name you'd like -> Create organization

To create a project, go to https://app.terraform.io -> New -> Project -> Give it any name you'd like.

To create a workspace, go to https://app.terraform.io -> New -> Workspace -> CLI-Driven Workflow -> name it "<MY_PROJECT>-dev" (replace <MY_PROJECT> with the name you used above) -> Choose your project in the dropdown -> Create

Once your workspace is created, on the right, click `Manage Tags`, under Tag Key set the name of your project to what you used above, ignore Tag value, click Save. (Later on, this will go in your `./infra/main.tf` `workspaces.tags` property to let your CLI know it can use this workspace for your codebase.)

Since workspaces manage environments, we'll need to add some environment variables.
<br />
On the left sidebar, click Variables -> Add the following variables with their key and value:

| Key                   | Value                                                      | Select Variable Category | Sensitive       | Description                                                                                                                            |
|-----------------------|------------------------------------------------------------|--------------------------|-----------------|----------------------------------------------------------------------------------------------------------------------------------------|
| AWS_ACCESS_KEY_ID     | (set this to the value from the cat command you ran above) | Environment Variable     | Checkmark       | Allows Terraform to login to your AWS account                                                                                          |
| AWS_SECRET_ACCESS_KEY | (set this to the value from the cat command you ran above) | Environment Variable     | Checkmark       | Allows Terraform to login to your AWS account                                                                                          |
| TF_CLI_ARGS_plan      | -var-file "environments/dev.tfvars"                        | Environment Variable     | Don't checkmark | Tells this workspace to use your `./infra/environments/dev.tfvars` environment variables when running Terraform plan and then applying |

Now you have a `dev` workspace.

Create another workspace but name it `test`. Do everything the same except set `TF_CLI_ARGS_plan` to `-var-file "environments/test.tfvars"`. This makes sure it uses your test environment variables.

In your CLI, Login to Terraform Cloud
```
terraform login
```
This will open your browser prompting you to login. Once you do it will give you a token. Copy/paste it back into your CLI


### 2) Deploy Infrastructure to Your AWS Cloud

<!-- In `./infra/main.tf`, set workspaces.tags to the project name you just created -->
In `./infra/main.tf`, in the workspaces.tags array, set it to the same tag that you gave your workspaces in the steps above.

In `./infra/variables.tf`, set the values so they are unique in your Cloud environment. Most of these will be the names of your resources in AWS

Run these commands to deploy your infra
```
cd ./infra
terraform init
terraform workspace select <your_dev_workspace>
terraform apply
```

The first time you deploy, it may give you an error like this:
```
│ Error: updating API Gateway Stage failed: BadRequestException: CloudWatch Logs role ARN must be set in account settings to enable logging
│ 
│   with aws_api_gateway_method_settings.example,
│   on api-gateway.tf line 68, in resource "aws_api_gateway_method_settings" "example":
│   68: resource "aws_api_gateway_method_settings" "example" {
```
Simply re-run terraform apply and the error should go away. (More info on this error is mentioned in the Common Issues section.)

The console will log your API Gateway endpoint (your backend) and your Cloudfront domain name (your website)

### 3) Prepare Code

In `./ui/src/environments/dev.js`, set BASE_PATH to your new API Gateway endpoint

In `./backend/src/main/resrouces/dev.yml`, make sure the variables match what you set them to in `./infra/variables.tf`. If not, update them. For `dynamodbTable` make sure `-dev` is appended to the end.

In `./backend/src/main/resrouces/local.yml`, copy/paste your dev.yml config into here (since when you run the app locally, it will hit your Dev resources in AWS.)

Continue to the Deploy Backend and Deploy UI sections
<br />
When finished, you will see your live website in your browser



# Deploy Backend
Make sure you're in your ./backend directory with `cd ./backend`

Build your project with Maven
```
mvn package
```

Deploy the newly generated .jar file to your Lambda
<br />
<LAMBDA_NAME> is the name you set `lambda_name` to in `./infra/variables.tf`
```
aws lambda update-function-code --function-name <LAMBDA_NAME> --zip-file fileb://target/lambda-placeholder-code-1.0-SNAPSHOT.jar
```

If successful, the CLI will let you know. You can also log into AWS, go to your Lambda, and it will say Last Modified  a few seconds or minutes ago

# Deploy UI
Make sure you're in your ./ui directory with `cd ./ui`

If on Windows
```
npm install cross-env
```
Go to `package.json` and change `build:dev` to "cross-env REACT_APP_ENV=dev react-scripts build". Change the other build and start commands as well.

In React, build the project to generate a ./build directory
```
npm run build:dev
```

Get your <BUCKET_NAME>. It should be `s3_bucket_name`-`env` from your `./infra/variables.tf`. The name and env are appended to make the bucket unique in your Cloud environement. The CLI also should have printed it when you ran Terraform Apply. You can also go to the AWS Console -> S3 to see it.

Delete all current objects in the bucket
```
aws s3 rm s3://<BUCKET_NAME> --recursive
```

Upload the new ./build to the bucket
```
aws s3 cp ./build s3://<BUCKET_NAME>/ --recursive
```

If successful, the content inside of ./build will be uploaded to S3 (index.html, static/, etc).
<br />
CloudFront directs traffic to index.html.

# Run Locally

Before you can run these apps locally, make sure you follow the Setup section and deploy your infra to the cloud.

Even though this is local, you'll still be interacting with your AWS resources (such as DynamoDb) in the cloud. This serverless architecture is cheap (we're talking pennies) but it's still something to be aware of.

### 1) Backend

First, make sure you have Docker and AWS Sam CLI installed. Once you do, open Docker.

Cd into your ./backend directory with `cd ./backend`

Build your Lambda SAM configuration.
<br />
This will use your ./backend/template.yml file to generate an .aws-sam file.
<br />
(Note that each time you change your backend code, you'll need to run these next two commands again.)
```
sam build
```

Now run your app locally on port 8080
```
sam local start-api -p 8080
```

The CLI will print the address:port the app is running on.
<br />

Your new local endpoint will be that address:port with "/local/add" appended. (For example: http://127.0.0.1:8080/local/add)

To test it, use curl:
```
curl <Replace_With_Local_Endpoint>
```
If successful, you should see a response similar to `{ "message": "hello world", "version": "7" }`

### 2) UI

Cd into your `./ui directory` with `cd ./ui`

Install dependencies (if you haven't already)
```
npm install
```

In `./ui/src/environments/local.js`, set ADD_ENDPOINT to the backend's local endpoint you generated above.

Run the app using the local profile
```
npm run start:local
```

This will open a new Browser window and you should see everything running as normal.
<br />
If you see an error, refresh the page. Sometimes it glitches when first booting up.


# Common Issues

### Problem: My website shows this error on the page:
```
<Error>
    <Code>AccessDenied</Code>
    <Message>Access Denied</Message>
    <RequestId>KR1QVB45342YNH42</RequestId>
    <HostId>elyCRDKZ//cHoWNikfoWDmlycdOxEJ0W8+NvuTmuA/w4RGCaat5XFBBiqPPPlrnJ1ga/FWrd5ZD2cqIuyc3AdA==</HostId>
</Error>
```
Solution: Your S3 bucket is empty. Deploy code to your bucket and you should see your UI appear.
<br />
If that doesn't work, something may be wrong with your IAM roles/policies or your CloudWatch configuration.

### Problem: Terraform Apply prints this in the CLI
```
Warning: This plan was generated using a different version of Terraform, the diff presented here may be missing representations of recent features.
```
Solution: This error occurs when your CLI Terraform version is different from your Terraform Cloud version. But because Terraform Cloud is doing all of the work, I don't think this is anything to worry about. It's just letting you know that the Plan it's displaying may not be the exact plan you'll see in the Terraform Cloud console.

### Problem: Terraform CLI gives you this error (1)
```
│ Error: Failed to decode current backend config
│ 
│ The backend configuration created by the most recent run of "terraform init" could not be decoded: EOF. The configuration may have been initialized by an earlier version that used an incompatible
│ configuration structure. Run "terraform init -reconfigure" to force re-initialization of the backend.
```
Solution: This occured for me when I changed the name of variable in all places in Intellij. It asked me if I wanted to change it in all places. I said yes. And then realized it was modifying variables in ./infra/./terraform for some reason.
<br />
Delete your `./infra/./terraform` folder. Then re-run `terraform init`. This is safe because all of your state is stored in Terraform Cloud.

### Problem: Terraform CLI gives you this error (2)
```
│ Error: updating API Gateway Stage failed: BadRequestException: CloudWatch Logs role ARN must be set in account settings to enable logging
│ 
│   with aws_api_gateway_method_settings.example,
│   on api-gateway.tf line 68, in resource "aws_api_gateway_method_settings" "example":
│   68: resource "aws_api_gateway_method_settings" "example" {
│ 
```
<!-- Solution: This occured the first time I ran terraform to a new env. Just re-run it  -->
Solution: Just re-run terraform and it should work. Sometimes Terraform provisions resources out of order when one is a dependency of another. In this case, it provisioned CloudWatch Logs Role before provisioning the account setting that allows that role. Often if you get an error in Terraform, re-running it will fix it.

### Problem: Lambda fails on the first call, then succeeds after.
Solution: Most likely the Lambda is timing out (you can verify this in the API Gateway Cloudwatch logs). Increasing the timeout may help (though it never did for me.) The solution that did work was increasing Lambda's memory. The more memory, the faster Lambda runs. Especially if it's communicating with other AWS services such as S3 or DynamoDb. See the chart on this page to see how memory size decreases a Lambda's duration: https://docs.aws.amazon.com/lambda/latest/operatorguide/computing-power.html

### Problem: Lambda takes too long on the first call
Solution: Most likely increasing the Lambda's memory will fix this. (See above problem/solution for more info.)
