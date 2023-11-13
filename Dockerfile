FROM maven@sha256:bc229c50a02b3c17c09085b9fceca1fcf44392f463d650e8e64bcabbc9ffd58a

WORKDIR /usr/local/diskUsing/wsp/currApp
COPY ./target/JavaParserSub-0.0.1.jar ./

EXPOSE 18082
CMD ["java", "-jar", "./JavaParserSub-0.0.1.jar"]
