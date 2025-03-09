FROM adoptopenjdk/openjdk11:jre-11.0.13_8-alpine
WORKDIR /opt/exathreat/intel/
ADD build/libs/exathreat-intel.jar /opt/exathreat/intel/
EXPOSE 8080
ENTRYPOINT [ "java", \
	"-Djava.security.egd=file:/dev/./urandom", \
	"-Dspring.profiles.active=${PROFILE}", \
	"-Des.domain=${ES_DOMAIN}", \
  "-Des.port=${ES_PORT}", \
  "-Des.scheme=${ES_SCHEME}", \
	"-jar", \
	"exathreat-intel.jar" ]