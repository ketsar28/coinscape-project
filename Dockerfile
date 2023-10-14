FROM openjdk:11-jdk
ADD target/coinscape-0.0.1-SNAPSHOT.jar coinscape-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "/coinscape-0.0.1-SNAPSHOT.jar"]