# Deployment guide using AWS Cloud Formation

## Build and publish the Docker image
1. Login to GitHub Container Registry:
   ```bash
      echo <GITHUB_TOKEN> | docker login ghcr.io -u jakubkindracki --password-stdin
   ```
2. Build the Docker image:
   ```bash
      docker build -t ghcr.io/<GITHUB_USERNAME>/hobbymatch:latest .
   ```
3. Push the Docker image to GitHub Container Registry:
   ```bash
      docker push ghcr.io/<GITHUB_USERNAME>/hobbymatch:latest
   ```
4. Change the image visibility on GitHub to public.

## Deploy using AWS CloudFormation
1. Set Up AWS CLI:
   - Install the AWS CLI if you haven't already.
   - Configure it with your AWS credentials:
     ```bash
     aws configure
     ```
2. Set AWS credentials:
    ```bash
    aws configure set aws_access_key_id=...
    aws configure set aws_secret_access_key=...
    aws configure set aws_session_token=...
    ```
3. Test the connection:
   ```bash
   aws sts get-caller-identity
   ```
4. Create the ssh key pair:
   ```bash
   aws ec2 create-key-pair --key-name hobbymatch-key --query 'KeyMaterial' --output text > hobbymatch-key.pem
   chmod 400 hobbymatch-key.pem
   ```
5. Set up the parameters.json file with your configuration.
6. Deploy the CloudFormation stack:
   ```bash
   aws cloudformation deploy \
    --template-file hobbymatch-infrastructure.yml \
    --stack-name hobbymatch-stack \
    --capabilities CAPABILITY_NAMED_IAM \
    --parameter-overrides file://parameters.json
   ```