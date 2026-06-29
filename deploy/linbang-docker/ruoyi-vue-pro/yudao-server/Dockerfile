FROM eclipse-temurin:8-jre

RUN mkdir -p /yudao-server
WORKDIR /yudao-server

COPY ./target/yudao-server.jar app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"
ENV ARGS=""

EXPOSE 48080

CMD java ${JAVA_OPTS} -jar app.jar $ARGS
