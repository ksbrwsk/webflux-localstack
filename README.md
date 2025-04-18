## webflux-localstack

### Example using Spring webflux on localstack s3 service and localstack testcontainer.

### Prerequisites:

* [Java 21](https://adoptium.net/)
* [Apache Maven](https:http://maven.apache.org/)
* [Docker](https://www.docker.com/)
* [Python](https://www.python.org/)
* [Localstack](https://github.com/localstack/localstack)
* [awscli-local](https://github.com/localstack/awscli-local)

### Install Localstack and awslocal-cli:
```bash
pip install localstack   #pip command depends on Python version, can be pip3 
pip install awscli-local
```

Setting up local region and credentials to run LocalStack
aws requires the region and the credentials to be set in order to run the aws commands. 
Create the default configuration & the credentials. 
Below key will ask for the Access key id, secret Access Key, region & output format.
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
$ awslocal s3api create-bucket --bucket sabo-s3-bucket
$ awslocal s3api list-buckets
```
### Edit application configuration to edit localstack properties (src/main/reources/application.yml)
```yaml
cloud:
  aws:
    region: eu-central-1
    s3:
      url: http://127.0.0.1:4566
      access-key: test
      secret-key: test
      bucket-name: sabo-s3-bucket
```
### Build & run the application
```bash
./mvnw package
java -jar ./target/weflux-localstack-2.1.0.jar
```
### List files in bucket 
```bash
http :8080/api/s3
```

### Upload file to bucket
```bash
http -f post :8080/api/s3/upload files@~/aaa.txt
```

### Download file from bucket
```bash
http :8080/api/s3/download/aaa.txt
```