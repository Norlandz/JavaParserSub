FROM eclipse-temurin@sha256:2419c9c7116601aee0c939888e2eed78e235d38f5f5e9e9f1d1d18d043df55eb

WORKDIR /usr/local/diskUsing/wsp/currApp
COPY ./target/JavaParserSub-0.0.1.jar ./

EXPOSE 18082
CMD ["java", "-jar", "./JavaParserSub-0.0.1.jar"]
