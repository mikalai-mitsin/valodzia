FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/valodzia-0.1.1.jar /app/valodzia.jar
ENTRYPOINT ["java","-jar","/app/valodzia.jar", "com.somepackage.SomeClass"]
