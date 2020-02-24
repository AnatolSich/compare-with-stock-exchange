FROM hirokimatsumoto/alpine-openjdk-11
COPY ./build/libs/upstox-compare-with-stock-exchange-1.0-SNAPSHOT.jar /app/upstox-compare-with-stock-exchange.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/upstox-compare-with-stock-exchange.jar"]