exathreat-intel
---------------

This service uses MAXMIND GeoIP2 databases.
To download the latest, please visit: https://www.maxmind.com/en/accounts/522260/geoip/downloads

To build your application:
*. ./gradlew clean build

To build your docker image: 
*. docker build -t exathreat-intel .

To list your docker images:
*. docker image list | grep exathreat-intel

To run your docker image with an active spring profile:
*. docker run -e PROFILE=local -e ES_DOMAIN=192.168.1.82 -e ES_PORT=9200 -e ES_SCHEME=http -p 8082:8082 exathreat-intel

To remove your dangling images:
*. docker rmi -f $(docker images --filter dangling=true)

To authenticate docker to your AWS ECR registry:
*. aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com

---

To tag your docker image to your ECR repository in AWS:
*. docker tag exathreat-intel 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-intel

To push your tagged docker image to your ECR repository in AWS:
*. docker push 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-intel

To pull your docker image from your ECR repository in AWS:
*. docker pull 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-intel
