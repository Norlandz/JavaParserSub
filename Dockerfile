FROM openjdk@sha256:d732b25fa8a6944d312476805d086aeaaa6c9e2fbc3aefd482b819d5e0e32e10

WORKDIR /usr/local/diskUsing/wsp/currApp
COPY ./target/JavaParserSub-0.0.1.jar ./

EXPOSE 18082
CMD ["java", "-jar", "./JavaParserSub-0.0.1.jar"]
