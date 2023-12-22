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
## 1. Prepare Terraform

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
<br />
<br />
<br />


## 2. Deploy the Backend (for the first time)

In ./infra-backend/main.tf, set workspaces.name to the backend workspace you created
<br />
In ./infra-backend/variables.tf, set the values so they are unique in your Cloud environement
<br />
Run these commands to deploy your backend infra
```
cd infra-backend
terraform init
terraform apply
```

The console will log your new API Gateway endpoint.
<br />
<br />
<br />

## 3. Deploy the UI (for the first time)

In ./ui/src/environments/dev.js, set BASE_PATH to your new API Gateway endpoint
<br />
In ./infra-ui/main.tf, set workspaces.name to the UI workspace you created
<br />
In ./infra-ui/variables.tf, set the values so they are unique in your Cloud environment
<br />
Run these commands to deploy your UI infra
```
cd infra-ui
terraform init
terraform apply
```
The CLI will log your website URL

Now build and deploy the UI code to the new bucket
```
cd ../ui
npm install
npm run build:dev
aws s3 cp ./build s3://<BUCKET_NAME>/ --recursive
```

Go to your website and you'll see your UI deployed, along with "hello world" being fetched from your backend Lambda
<br />
`{"message":"hello world","version":"1"}`

Congrats!

These setup commands should only be used the first time your deploy to AWS. 

From here on out, use the Common Commands section to continue deploying any changes you make.
<br />
<br />
<br />

# Common Commands

(This section is a work in progress)

## Deploy Backend
```
aws lambda update-function-code --function-name <LAMBDA_NAME> --zip-file fileb://backend/target/lambda-placeholder-code-1.0-SNAPSHOT.jar
```

## Deploy UI
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

## Backend

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

# UI

Cd into your ./ui directory with `cd ./ui`

In ./ui/src/environments/local.js, set ADD_ENDPOINT to the backend's local endpoint you generated above.

Run the app using the local profile
```
npm run start:local
```

This will open a new Browser window and you should see everything running as normal.
<br />
If you see an error, refresh the page. Sometimes it glitches when first booting up.
