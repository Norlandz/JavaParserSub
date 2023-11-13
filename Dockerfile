FROM maven@sha256:0b27c7feef457b6773e078b0ab679d97a471d9fdebd07df3f9b0cdc762c5b4a6

WORKDIR /usr/local/diskUsing/wsp/currApp
COPY ./target/JavaParserSub-0.0.1.jar ./

EXPOSE 18082
CMD ["java", "-jar", "./JavaParserSub-0.0.1.jar"]
