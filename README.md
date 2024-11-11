#Tools used in this Project 

a. GitHub :- To ensure version control & keep the application code sync with AWS Developer services 
b. AWS ECR: Amazon Elastic Container Registry (ECR) acts as a Docker-compatible container registry where we can push, pull, and store container images. It allows secure storage and retrieval of container images for containerized applications. We can use Docker CLI commands to log in to ECR, build images locally, and push them into ECR for deployment.

c. AWS ECS: Amazon Elastic Container Service (ECS) is a container orchestration platform that simplifies running and managing containers without requiring direct management of the underlying infrastructure such as compute, networking, and storage. ECS supports both EC2-based and serverless deployments through AWS Fargate, enabling applications to run without provisioning or managing servers.

 # Cluster: A group of containers managed together within ECS. Clusters provide logical isolation and group all containerized resources under a single namespace.

  # Task Definition: A task definition is a template defining parameters for running containers in the cluster, such as CPU and memory allocation, storage, network configurations, and container image details. Task definitions also allow you to specify the compute environment (like EC2 or Fargate) and any container dependencies.

  # Task: A task represents an instance of a task definition. Each task runs one or more containers defined in the task definition. In a microservices architecture, each task often corresponds to a single microservice component. Tasks can run individually or be managed as part of a service.

  # Service: Services manage and scale tasks within an ECS cluster. They maintain the desired count of tasks, ensure high availability, and distribute tasks across Availability Zones (AZs). Services can also be configured with load balancers to expose application endpoints to external traffic. In this project, the service listens on port 8080, enabling consistent access to each task regardless of the dynamic IPs assigned.

d. AWS Code Build :- This services will ensure of all steps belongs to Continuous Integration where it includes Build , Test of mvn spring boot application to execute mvn clean install . 
# Clean: The clean phase deletes any previously compiled files and output directories (typically the target/ folder). This step ensures a fresh start, preventing any issues caused by leftover files from previous builds.

# Install: The install phase compiles the code, runs tests, packages the application (into a JAR or WAR file, depending on the configuration), and places the packaged application in the local Maven repository. This makes it accessible for other projects if needed as a dependency.

# 1. Pre-Build 
In this phase, we set up the environment and log in to Amazon ECR.

Logging in to Amazon ECR:
aws --version checks if the AWS CLI is installed and returns the version for verification.
The get-login-password command generates a login password and pipes it into Docker to authenticate (docker login) with ECR. This allows Docker to push images to the ECR repository later.
Setting Variables:
REPOSITORY_URI: Defines the URI of the ECR repository where the Docker images will be stored.
IMAGE_TAG: Creates a unique tag for each build, using the build ID from CodeBuild to help identify and track builds.

# 2. Build Phase
In this phase, we build the Spring Boot application and the Docker image.

Build the JAR File:

mvn clean install compiles the application, runs tests, and packages it into a JAR file located in the target directory.
Build the Docker Image:

docker build -t $REPOSITORY_URI:latest .: Builds the Docker image, tagging it as latest with the specified repository URI.
docker tag $REPOSITORY_URI:latest $REPOSITORY_URI:$IMAGE_TAG: Tags the Docker image with a unique tag (build-<build-id>) generated in the pre-build phase, so we can distinguish this image from others.

# 3. Post-Build Phase
In this phase, we push the Docker images to ECR and prepare an image definition file.

Push Docker Images to ECR:

docker push $REPOSITORY_URI:latest: Pushes the latest tagged Docker image to the specified ECR repository.
docker push $REPOSITORY_URI:$IMAGE_TAG: Pushes the uniquely tagged Docker image (with the build ID) to the repository.
Create Image Definitions File:

DOCKER_CONTAINER_NAME=spring-demo-ecr: Sets the container name to be used in the image definitions file.
printf '[{"name":"%s","imageUri":"%s"}]' $DOCKER_CONTAINER_NAME $REPOSITORY_URI:$IMAGE_TAG > imagedefinitions.json: Generates an imagedefinitions.json file. This file tells CodePipeline which Docker image to deploy to ECS by specifying the container name and image URI.
cat imagedefinitions.json: Prints out the contents of the imagedefinitions.json file for debugging and verification.

# 4. Artifacts Section
This section specifies which files should be saved and passed to CodePipeline.

imagedefinitions.json: Required by CodePipeline for ECS deployments, to know which image to deploy.
target/springboot-aws-deploy.jar: The packaged JAR file of the Spring Boot application.

e. AWS Code Pipeline :- This is an orchestrated Pipeline tool which covers from code version to Code Deploy everything under a unified single pane of glass . The previous step belongs to Build the application as compile , packaged the code and pushes into the ECR . Now to deploy we want to perform the same in a container orchestartion platform such as ECS .
So we have a seperte category of AWS Code Deploy into ECS . Defined the stages , source & the most important thing is permissions . 
There are two permissions :-

Code Build service role :- ECR Full Registry access require to be provide to avoid Exit status 1 error .
Code Pipeline service role :- This role will actualls integrate with ECS tasks to successfully deploy the application into ECS . To validate or confirm the same , we will check the Definition No as incremental whenever the Code Pipline runs and generate & tagged a new image to ECR . This resulted an unique instances of IP address on where by the respectuve port wecan access the application or any API endpoint .

![image](https://github.com/user-attachments/assets/fcd3246d-a650-4267-966a-3a03641fbc1a)




