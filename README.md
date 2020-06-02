# content-service

This service is responsible for platform contents CRUD operations that will be used as API Gateway to trigger Lambdas and write in a dynamoDb and a kinesis stream.

The App is deployed as a zipped classes directory through maven assembly plugin on an S3 bucket as a lambda and triggered through API endpoints

## Local development

Please install Lombok plugin for your IDE & you will need to add the following to debug or start:

    VM options: -Dspring.profiles.active=dev
    Environment variables: AWS_DEFAULT_REGION=eu-west-1

## Using Docker to simplify local development (kinesis not finalized)

You can use Docker to ease local development experience. Use [ops/docker/dynamodb-local.yml](ops/docker/dynamodb-local.yml) & [ops/docker/kinesis-local.yml](ops/docker/kinesis-local.yml) files to launch required third party services.

To Build docker image for the first time (kinesis-local not finalized):

    docker-compose -f ops/docker/dynamodb-local.yml up --build
    docker-compose -f ops/docker/kinesis-local.yml up --build

To Start docker image without sudo:

    sudo chmod 777 /var/run/docker.sock

For example, to start a local dynamoDb & kinesis in  docker containers, run:

    docker-compose -f ops/docker/dynamodb-local.yml up -d
    docker-compose -f ops/docker/kinesis-local.yml up -d

To stop it and remove containers, run:

    docker-compose -f ops/docker/dynamodb-local.yml down
    docker-compose -f ops/docker/kinesis-local.yml down
