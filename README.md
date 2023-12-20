## Setup

On Mac, run this command to get your AWS Access Key and Secret Key
(If the file shows more than one user, more than likely you want the values for the default user)
```
cat /Users/michaelmcmasters/.aws/credentials
```

Go to Terraform Cloud: https://app.terraform.io/app/mcmasters/workspaces
Create two new workspaces, one for the Backend and one for the UI
In *each* workspace, on the left sidebar, click "Variables".

Add AWS_ACCESS_KEY_ID,
Set it to aws_access_key_id's value (from the cat command above),
Click Environment Variable,
Check "sensitive"

Add AWS_SECRET_ACCESS_KEY,
Set it to aws_secret_access_key's value (from the cat command above),
Click Environment Variable,
Check "sensitive"

Login to Terraform Cloud from your CLI
```
terraform login
```
It will open the browser, prompting you to login (userId: jmcmasters411), then will give you a token that you paste back into your CLI


## Deploy the Backend

In ./infra-backend/main.tf, set workspaces.name to the workspace you created
In ./infra-backend/variables.tf, set the values so they are unique in your Cloud environement
cd into ./infra-ui, initialize Terraform, and apply
```
cd infra-backend
terraform init
terraform apply
```


## Deploy the UI

In ./infra-ui/main.tf, set workspaces.name to the workspace you created
In ./infra-ui/variables.tf, set the values so they are unique in your Cloud environement
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
{"message":"hello world","version":"1"}