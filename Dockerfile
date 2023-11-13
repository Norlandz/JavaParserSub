FROM openjdk@sha256:779635c0c3d23cc8dbab2d8c1ee4cf2a9202e198dfc8f4c0b279824d9b8e0f22

WORKDIR /usr/local/diskUsing/wsp/currApp
COPY ./target/JavaParserSub-0.0.1.jar ./

EXPOSE 18082
CMD ["java", "-jar", "./JavaParserSub-0.0.1.jar"]
