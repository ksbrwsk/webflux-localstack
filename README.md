https://github.com/localstack/localstack

```bash
pip install localstack
pip install awscli-local
```

### Configure AWS Credentials and region
```bash
aws configure --profile default
```

### Config & credential file will be created under ~/.aws folder

NOTE: Please use test as Access key id and secret Access Key to make S3 presign url work. 
We have added presign url signature verification algorithm to validate the presign url and its expiration. 
You can configure credentials into the system environment using export command in the linux/Mac system. 
You also can add credentials in 
```bash
~/.aws/credentials 
```
file directly.
```bash
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
```
### Start Localstack with S3 service
```bash
SERVICES=s3 DEFAULT_REGION=eu-central-1 DATA_DIR=~/localstack_data_dir localstack start
```
### Create Bucket
```bash
awslocal s3 mb s3://sabo-s3-bucket
```
### Edit application configuration to edit localstack properties
```bash
src/main/reources/application.yml

config:
  aws:
    region: eu-central-1
    s3:
      url: http://127.0.0.1:4566    #Default Localstack URL
      bucket-name: sabo-s3-bucket   #Name of the bucket you created previously
      access-key: test
      secret-key: test

```

### Upload file eg (using HTTPie)
```bash
http -f post :8080/api/s3/upload files@~/aaa.txt
```