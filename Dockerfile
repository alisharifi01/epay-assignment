FROM openjdk:8-jdk-alpine
MAINTAINER Ali Sharifi <alisharifi01@gmail.com>
ENTRYPOINT ["/usr/bin/java", "-jar", "/opt/payment.jar"]
ARG JAR_FILE
EXPOSE 8080
ADD ${JAR_FILE} /opt/payment.jar