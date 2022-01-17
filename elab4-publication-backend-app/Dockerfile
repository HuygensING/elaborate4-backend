FROM openjdk:8-jre-slim

ADD target/dropwizard-kotlin-app.jar /data/dropwizard-kotlin-app.jar

ADD src/main/resources/app-config.yml /data/app-config.yml

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap \
       -jar /data/dropwizard-kotlin-app.jar server /data/app-config.yml

EXPOSE 8080