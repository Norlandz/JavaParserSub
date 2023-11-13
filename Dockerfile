FROM maven@sha256:76b11de3a90a9dd4b2b1765850087296ec630c16636c91f0181d2fb7859f8502

WORKDIR /usr/local/diskUsing/wsp/currApp
COPY ./target/JavaParserSub-0.0.1.jar ./

EXPOSE 18082
CMD ["java", "-jar", "./JavaParserSub-0.0.1.jar"]
