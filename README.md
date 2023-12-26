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

# Setup
### 1) Prepare Terraform

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

To create a workspace, go to https://app.terraform.io -> New -> Workspace -> CLI-Driven Workflow -> name it "dev" -> Choose your project in the dropdown -> Create

Once your workspace is created, on the right look for the `tags` dropdown, click it, type the name of your project and hit enter. This will tell our CLI and ./infra repository that it can use this workspace.

Since workspaces manage environments, we'll need to add some environment variables.
<br />
On the left sidebar, click Variables -> Add the following variables with their key and value:

 Key                   | Value                                                      | Additional                                          | Description                                                                                                                           
-----------------------|------------------------------------------------------------|-----------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------
 AWS_ACCESS_KEY_ID     | (set this to the value from the cat command you ran above) | Select `Environment Variable` and check `Sensitive` | Allows Terraform to login to your AWS account                                                                                         
 AWS_SECRET_ACCESS_KEY | (set this to the value from the cat command you ran above) | Select `Environment Variable` and check `Sensitive` | Allows Terraform to login to your AWS account                                                                                         
 TF_CLI_ARGS_plan      | -var-file "environments/dev.tfvars"                        |                                                     | Tells this workspace to use your ./infra/environments/dev.tfvars environment variables when running Terraform plan and then applying 

Now you have a `dev` workspace.

Create another workspace but name it `test`. Do everything the same except set `TF_CLI_ARGS` to `-var-file "environments/test.tfvars"`. This makes sure it uses your test environment variables.

In your CLI, Login to Terraform Cloud
```
terraform login
```
This will open your browser prompting you to login. Once you do it will give you a token. Copy/paste it back into your CLI


### 2) Deploy Infrastructure to Your AWS Cloud

In `./infra/main.tf`, set workspaces.name to the workspace name you just created

In `./infra/variables.tf`, set the values so they are unique in your Cloud environement. Most of these will be the names of your resources in AWS

Run these commands to deploy your infra
```
cd ./infra
terraform init
terraform apply
```

The console will log your API Gateway endpoint (your backend) and your Cloudfront domain name (your website)

### 3) Prepare Code

In `./ui/src/environments/dev.js`, set BASE_PATH to your new API Gateway endpoint

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
```
aws lambda update-function-code --function-name <LAMBDA_NAME> --zip-file fileb://backend/target/lambda-placeholder-code-1.0-SNAPSHOT.jar
```

If successful, the CLI will let you know. You can also log into AWS, go to your Lambda, and it will say Last Modified  a few seconds or minutes ago

# Deploy UI
Make sure you're in your ./ui directory with `cd ./ui`

In React, build the project to generate a ./build directory
```
npm run build:dev
```

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


# Common Errors

Problem: My website shows this error in the browser
```
<Error>
    <Code>AccessDenied</Code>
    <Message>Access Denied</Message>
    <RequestId>KR1QVB45342YNH42</RequestId>
    <HostId>elyCRDKZ//cHoWNikfoWDmlycdOxEJ0W8+NvuTmuA/w4RGCaat5XFBBiqPPPlrnJ1ga/FWrd5ZD2cqIuyc3AdA==</HostId>
</Error>
```
Solution: Your S3 bucket is empty. Deploy code to your bucket and you should see your UI appear.
If that doesn't work, something may be wrong with your IAM roles/policies or your CloudWatch configuration.
